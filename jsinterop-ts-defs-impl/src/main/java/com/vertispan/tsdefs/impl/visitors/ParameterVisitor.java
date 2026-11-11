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
import com.vertispan.tsdefs.impl.builders.HasParameters;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsParameter;
import com.vertispan.tsdefs.impl.model.TsType;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import jsinterop.annotations.JsProperty;

public class ParameterVisitor<T> extends TsElement {

  public ParameterVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  /**
   * Visits a parameter of a method inherited by {@code inheritedBy}, or null.
   *
   * @param typeArguments what each of the method's type parameters is emitted as
   */
  public ParameterVisitor(
      Element element,
      TypeElement inheritedBy,
      Map<Element, TsType> typeArguments,
      HasProcessorEnv env) {
    super(element, inheritedBy, typeArguments, env);
  }

  /** @param typeArguments what each type parameter is emitted as, keyed by its element */
  public ParameterVisitor(
      Element element, Map<Element, TsType> typeArguments, HasProcessorEnv env) {
    super(element, typeArguments, env);
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
    TsType type = getType();
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
