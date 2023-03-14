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
import com.vertispan.tsdefs.builders.HasTypeArguments;
import com.vertispan.tsdefs.builders.JavaToTsTypeConverter;
import com.vertispan.tsdefs.builders.TsElement;
import com.vertispan.tsdefs.model.TsType;
import java.util.List;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;

public class MethodTypeArgumentsVisitor<T> extends TsElement {
  private final ExecutableElement element;

  public MethodTypeArgumentsVisitor(ExecutableElement element, HasProcessorEnv env) {
    super(element, env);
    this.element = element;
  }

  public void visit(HasTypeArguments<T> builder) {
    List<? extends TypeParameterElement> typeParameters = element.getTypeParameters();
    typeParameters.forEach(
        typeElement -> {
          TsType type = TsElement.of(typeElement, env).getType();
          if (typeElement.getKind().equals(ElementKind.TYPE_PARAMETER)) {
            List<? extends TypeMirror> bounds = typeElement.getBounds();

            bounds.stream()
                .filter(
                    boundType -> !JavaToTsTypeConverter.isSameType(boundType, Object.class, env))
                .map(boundType -> TsElement.of(boundType, env).getType())
                .forEach(type::addBounds);
          }
          builder.addTypeArgument(type);
        });
  }
}
