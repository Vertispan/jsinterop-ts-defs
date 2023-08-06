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
import com.vertispan.tsdefs.builders.HasFunctions;
import com.vertispan.tsdefs.builders.TsElement;
import com.vertispan.tsdefs.model.TsDoc;
import com.vertispan.tsdefs.model.TsMethod;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class ClassMethodVisitor<T> extends TsElement {
  private final boolean copyDocs;

  public ClassMethodVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
    this.copyDocs = true;
  }

  public ClassMethodVisitor(Element element, HasProcessorEnv env, boolean copyDocs) {
    super(element, env);
    this.copyDocs = copyDocs;
  }

  public void visit(HasFunctions<T> parent) {

    if (isMethod()
        && !isIgnored()
        && !isOverlay()
        && !isJsProperty()
        && isExportable()
        && !isGetter()
        && !isSetter()
        && (isJsMethod() || (parent().isJsType() && isPublic()))) {

      TsElement parentElement = TsElement.of(element.getEnclosingElement(), env);
      if (parentElement.isTsInterface()
          && parentElement.isInheritedMethod((ExecutableElement) this.element)) {
        return;
      }

      TsMethod.TsMethodBuilder builder =
          TsMethod.builder(getName(), getType())
              .addModifiers(getJsModifiers())
              .setDocs(copyDocs ? getDocs() : TsDoc.empty())
              .setDeprecated(isDeprecated());

      ExecutableElement executableElement = (ExecutableElement) element;
      new MethodTypeArgumentsVisitor<TsMethod.TsMethodBuilder>(executableElement, env)
          .visit(builder);
      executableElement
          .getParameters()
          .forEach(
              param -> new ParameterVisitor<TsMethod.TsMethodBuilder>(param, env).visit(builder));
      parent.addFunction(builder.build());
    }
  }
}
