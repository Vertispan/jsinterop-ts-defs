/*
 * Copyright © 2023 Vertispan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertispan.tsdefs.visitors;

import com.vertispan.tsdefs.HasProcessorEnv;
import com.vertispan.tsdefs.builders.HasInterfaces;
import com.vertispan.tsdefs.builders.TsElement;
import com.vertispan.tsdefs.model.TsInterface;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class InterfacesVisitor<T> extends TsElement {
  public InterfacesVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(HasInterfaces<T> builder) {
    List<? extends TypeMirror> interfaces = ((TypeElement) element).getInterfaces();

    interfaces.stream()
        .filter(e -> TsElement.of(e, env).isExportable() && !TsElement.of(e, env).isTsIgnored())
        .forEach(interfaceElement -> addInterface(builder, interfaceElement));
  }

  private void addInterface(HasInterfaces<T> builder, TypeMirror interfaceElement) {
    TsElement tsElement = TsElement.of(interfaceElement, env);
    if (!tsElement.isTsIgnored()) {
      TsInterface.TsInterfaceBuilder interfaceBuilder =
          TsInterface.builder(tsElement.getName(), tsElement.getNamespace());
      new TypeArgumentsVisitor<TsInterface.TsInterfaceBuilder>(interfaceElement, env)
          .visit(interfaceBuilder);
      builder.addInterface(interfaceBuilder.build());
    }
  }
}
