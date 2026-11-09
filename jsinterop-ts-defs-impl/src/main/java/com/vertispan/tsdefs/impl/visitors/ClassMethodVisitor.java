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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;

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
    this(element, inheritedBy, copyDocs, renamedTypeParameters(element, inheritedBy), env);
  }

  /**
   * New names for the type parameters of {@code method}, inherited by {@code inheritedBy}, that
   * share a name with one of {@code inheritedBy}'s: substituted arguments can name those, and the
   * method's own would capture them, e.g. {@code <U> U conv(T)} with {@code T} as {@code U[]}.
   */
  private static Map<Element, TsType> renamedTypeParameters(
      Element method, TypeElement inheritedBy) {
    if (!(method instanceof ExecutableElement)) {
      return Collections.emptyMap();
    }
    List<? extends TypeParameterElement> parameters =
        ((ExecutableElement) method).getTypeParameters();
    Set<String> taken = new HashSet<>();
    inheritedBy.getTypeParameters().forEach(p -> taken.add(p.getSimpleName().toString()));
    parameters.forEach(p -> taken.add(p.getSimpleName().toString()));
    Map<Element, TsType> renamed = new HashMap<>();
    for (TypeParameterElement parameter : parameters) {
      String name = parameter.getSimpleName().toString();
      if (inheritedBy.getTypeParameters().stream()
          .anyMatch(p -> p.getSimpleName().contentEquals(name))) {
        int suffix = 1;
        while (taken.contains(name + suffix)) {
          suffix++;
        }
        taken.add(name + suffix);
        renamed.put(parameter, TsType.of(name + suffix, ""));
      }
    }
    return renamed;
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

      TsElement parentElement = TsElement.of(element.getEnclosingElement(), env);
      if (parentElement.isTsInterface()
          && parentElement.isInheritedMethod((ExecutableElement) this.element)) {
        return;
      }
      parent.addFunction(toTsMethod());
    }
  }

  private TsMethod toTsMethod() {
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
    return builder.build();
  }
}
