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
import com.vertispan.tsdefs.builders.HasParameters;
import com.vertispan.tsdefs.builders.TsElement;
import com.vertispan.tsdefs.model.TsParameter;
import com.vertispan.tsdefs.model.TsType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import jsinterop.annotations.JsProperty;

public class ParameterVisitor<T> extends TsElement {
  private final Map<String, TsType> typeArgsMapping;

  public ParameterVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
    this.typeArgsMapping = new HashMap<>();
  }

  public ParameterVisitor(
      Element element, Map<String, TsType> typeArgsMapping, HasProcessorEnv env) {
    super(element, env);
    this.typeArgsMapping = typeArgsMapping;
  }

  @Override
  public String getName() {
    Element enclosingElement = element.getEnclosingElement();
    if (enclosingElement instanceof ExecutableElement) {
      Element parentClass = enclosingElement.getEnclosingElement();
      Optional<? extends Element> first =
          parentClass.getEnclosedElements().stream()
              .filter(
                  e -> e.getKind().isField() && e.getSimpleName().equals(element.getSimpleName()))
              .findFirst();
      if (first.isPresent()) {
        JsProperty jsPropertyInfo = first.get().getAnnotation(JsProperty.class);
        return Optional.ofNullable(jsPropertyInfo)
            .map(JsProperty::name)
            .filter(name -> !name.trim().isEmpty() && !"<auto>".equals(name))
            .orElse(super.getName());
      }
    }
    return super.getName();
  }

  public void visit(HasParameters<T> parent) {
    TsType type;
    TypeMirror typeMirror = element.asType();
    if (TypeKind.TYPEVAR.equals(typeMirror.getKind())
        && typeArgsMapping.containsKey(element.asType().toString())) {
      type = typeArgsMapping.get(element.asType().toString());
    } else {
      type = getType();
    }
    ExecutableElement parentElement = (ExecutableElement) element.getEnclosingElement();

    parent.addParameter(
        TsParameter.builder(getName(), type)
            .addModifiers(getJsModifiers())
            .setOptional(isJsOptional())
            .setVarargs(
                parentElement.isVarArgs()
                    && (parentElement.getParameters().indexOf(element)
                        == (parentElement.getParameters().size() - 1)))
            .build());
  }
}
