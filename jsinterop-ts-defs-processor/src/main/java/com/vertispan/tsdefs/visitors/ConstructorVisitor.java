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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.vertispan.tsdefs.HasProcessorEnv;
import com.vertispan.tsdefs.builders.HasConstructor;
import com.vertispan.tsdefs.builders.TsElement;
import com.vertispan.tsdefs.exception.MultipleConstructorsException;
import com.vertispan.tsdefs.model.TsClass;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

public class ConstructorVisitor<T> extends TsElement {
  public ConstructorVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public String getName() {
    return "constructor";
  }

  public void visit(HasConstructor<T> tsClassBuilder) {
    if (isConstructor()
        && isPublic()
        && isNull(element.getAnnotation(JsIgnore.class))
        && (nonNull(element.getAnnotation(JsConstructor.class))
            || nonNull(element.getEnclosingElement().getAnnotation(JsType.class)))) {

      TsClass.TsConstructor.TsConstructorBuilder constructor =
          TsClass.constructorBuilder()
              .addModifiers(getJsModifiers())
              .setDocs(getDocs())
              .setDeprecated(isDeprecated());

      ExecutableElement executableElement = (ExecutableElement) element;
      executableElement
          .getParameters()
          .forEach(
              param ->
                  new ParameterVisitor<TsClass.TsConstructor.TsConstructorBuilder>(param, env)
                      .visit(constructor));

      try {
        tsClassBuilder.setConstructor(constructor.build());
      } catch (MultipleConstructorsException e) {
        env.messager()
            .printMessage(Diagnostic.Kind.ERROR, "Multiple constructors are not allowed", element);
      }
    }
  }
}
