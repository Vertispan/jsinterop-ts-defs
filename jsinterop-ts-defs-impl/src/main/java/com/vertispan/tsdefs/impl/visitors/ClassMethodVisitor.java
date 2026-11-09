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
import com.vertispan.tsdefs.impl.builders.HasFunctions;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsDoc;
import com.vertispan.tsdefs.impl.model.TsMethod;
import com.vertispan.tsdefs.impl.model.TsType;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class ClassMethodVisitor<T> extends TsElement {
  private final boolean copyDocs;
  private final TypeElement inheritedBy;
  private final Map<Element, TsType> renamed;

  public ClassMethodVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
    this.copyDocs = true;
    this.inheritedBy = null;
    this.renamed = Collections.emptyMap();
  }

  /** Visits a method inherited by {@code inheritedBy}. */
  public ClassMethodVisitor(
      Element element, TypeElement inheritedBy, boolean copyDocs, HasProcessorEnv env) {
    this(
        element, inheritedBy, copyDocs, TsElement.renamedTypeParameters(element, inheritedBy), env);
  }

  private ClassMethodVisitor(
      Element element,
      TypeElement inheritedBy,
      boolean copyDocs,
      Map<Element, TsType> renamed,
      HasProcessorEnv env) {
    super(element, inheritedBy, renamed, env);
    this.copyDocs = copyDocs;
    this.inheritedBy = inheritedBy;
    this.renamed = renamed;
  }

  public void visit(HasFunctions<T> parent) {

    if (isMethod() && !isJsProperty() && isExportable() && !isGetter() && !isSetter()) {

      Optional<ExecutableElement> wider = widerThanTypeScriptAllows();
      if (wider.isPresent()) {
        // A copy stands for the wider member chosen in its place; a declaration must declare it.
        if (inheritedBy == null) {
          new ClassMethodVisitor<T>(
                  wider.get(), (TypeElement) element.getEnclosingElement(), copyDocs, env)
              .visit(parent);
        }
        return;
      }
      // A copy into a subtype was already chosen as the most specific override.
      TsElement parentElement = TsElement.of(element.getEnclosingElement(), env);
      if (inheritedBy == null
          && parentElement.isTsInterface()
          && parentElement.isInheritedMethod((ExecutableElement) this.element)) {
        return;
      }

      TsMethod.TsMethodBuilder builder =
          TsMethod.builder(getName(), getType())
              .addModifiers(getJsModifiers())
              .setDocs(copyDocs ? getDocs() : TsDoc.empty())
              .setDeprecated(isDeprecated());

      ExecutableElement executableElement = (ExecutableElement) element;
      new MethodTypeArgumentsVisitor<TsMethod.TsMethodBuilder>(
              executableElement, inheritedBy, renamed, env)
          .visit(builder);
      executableElement
          .getParameters()
          .forEach(
              param ->
                  new ParameterVisitor<TsMethod.TsMethodBuilder>(param, inheritedBy, renamed, env)
                      .visit(builder));
      parent.addFunction(builder.build());
    }
  }
}
