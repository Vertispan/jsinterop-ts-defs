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

import static java.util.Objects.nonNull;

import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.HasFunctions;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsDoc;
import com.vertispan.tsdefs.impl.model.TsMethod;
import com.vertispan.tsdefs.impl.model.TsType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

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

      // A copy into a subtype was already chosen as the most specific override.
      TsElement parentElement = TsElement.of(element.getEnclosingElement(), env);
      if (inheritedBy == null
          && parentElement.isTsInterface()
          && restatesInheritedMethods(parentElement)) {
        return;
      }
      parent.addFunction(toTsMethod(copyDocs ? getDocs() : TsDoc.empty()));
    }
  }

  private TsMethod toTsMethod(TsDoc docs) {
    TsMethod.TsMethodBuilder builder =
        TsMethod.builder(getName(), getType())
            .addModifiers(getJsModifiers())
            .setDocs(docs)
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
    return builder.build();
  }

  /**
   * Whether this method, declared in the TsInterface {@code declaring}, only restates what it
   * overrides, so TypeScript already sees it there: no doc comment of its own, and every overridden
   * method is emitted in a supertype TypeScript sees, with the same Java signature as a member of
   * {@code declaring} and the same text when copied into it.
   */
  private boolean restatesInheritedMethods(TsElement declaring) {
    ExecutableElement method = (ExecutableElement) element;
    List<ExecutableElement> overridden = declaring.overriddenMethods(method);
    if (overridden.isEmpty() || hasOwnDocs(method)) {
      return false;
    }
    TypeElement declaringType = (TypeElement) declaring.element();
    HasProcessorEnv quiet = HasProcessorEnv.quiet(env);
    String emitted =
        new ClassMethodVisitor<T>(method, quiet).toTsMethod(TsDoc.empty()).emit("", "");
    return overridden.stream()
        .allMatch(
            superMethod ->
                isEmittedInSupertype(superMethod)
                    && env.types()
                        .isSameType(
                            method.asType(),
                            env.types()
                                .asMemberOf((DeclaredType) declaringType.asType(), superMethod))
                    && emitted.equals(
                        new ClassMethodVisitor<T>(superMethod, declaringType, false, quiet)
                            .toTsMethod(TsDoc.empty())
                            .emit("", "")));
  }

  /** A blank comment, or one that only inherits, says nothing of its own. */
  private boolean hasOwnDocs(ExecutableElement method) {
    String comment = env.elements().getDocComment(method);
    return nonNull(comment) && !comment.replace("{@inheritDoc}", "").trim().isEmpty();
  }

  /** Not when its type is a plain Java type or @TsIgnore, which TypeScript doesn't see. */
  private boolean isEmittedInSupertype(ExecutableElement superMethod) {
    TsElement superType = TsElement.of(superMethod.getEnclosingElement(), env);
    return TsElement.of(superMethod, env).isExportable()
        && superType.isExportable()
        && !superType.isTsIgnored();
  }
}
