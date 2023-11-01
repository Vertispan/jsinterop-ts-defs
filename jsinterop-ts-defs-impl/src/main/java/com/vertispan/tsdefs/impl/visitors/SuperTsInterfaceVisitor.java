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
package com.vertispan.tsdefs.impl.visitors;

import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.HasInterfaces;
import com.vertispan.tsdefs.impl.builders.IsClassBuilder;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsInterface;
import javax.lang.model.type.TypeMirror;

public class SuperTsInterfaceVisitor<T> extends TsElement {
  private final TypeMirror type;

  public SuperTsInterfaceVisitor(TypeMirror typeMirror, HasProcessorEnv env) {
    super(typeMirror, env);
    this.type = typeMirror;
  }

  public void visit(IsClassBuilder<T> builder) {
    if (isTsInterface()) {
      addInterface(builder, type);
    } else {
      getJavaSuperClass()
          .ifPresent(
              typeMirror -> {
                new SuperTsInterfaceVisitor<T>(typeMirror, env).visit(builder);
              });
    }
  }

  private void addInterface(HasInterfaces<T> builder, TypeMirror typeMirror) {
    TsElement tsElement = TsElement.of(typeMirror, env);
    if (!tsElement.isTsIgnored()) {
      TsInterface.TsInterfaceBuilder interfaceBuilder =
          TsInterface.builder(tsElement.getName(), tsElement.getNamespace());
      new TypeArgumentsVisitor<TsInterface.TsInterfaceBuilder>(typeMirror, env)
          .visit(interfaceBuilder);
      builder.addInterface(interfaceBuilder.build());
    }
  }
}
