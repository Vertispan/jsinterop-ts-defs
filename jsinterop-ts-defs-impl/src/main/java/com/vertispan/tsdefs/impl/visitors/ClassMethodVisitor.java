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
import com.vertispan.tsdefs.impl.model.TsNullableType;
import com.vertispan.tsdefs.impl.model.TsType;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;

public class ClassMethodVisitor<T> extends TsElement {
  /** A string, number or boolean literal type, as emitted. */
  /** A string literal type, as emitted. TODO numbers and booleans aren't checked. */
  private static final Pattern STRING_LITERAL = Pattern.compile("\".*\"");

  /** The type unknown, not a namespace segment, member or string literal with that name. */
  private static final Pattern UNKNOWN = Pattern.compile("(?<![\\w$.\"'])unknown(?![\\w$.\"'])");

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

      // A type's own declaration only: a copy is emitted next to the inherited member it overrides,
      // which is copied too. TODO a follow-up copies only the most specific override.
      if (inheritedBy == null) {
        reportWidening();
        TsElement parentElement = TsElement.of(element.getEnclosingElement(), env);
        // An interface inherits the wider declaration; a class that implements one inherits
        // nothing, and one left without members isn't emitted.
        if ((parentElement.isInterface() || parentElement.isTsInterface())
            && (narrowsBeyondTypeScript() || isLeftOutOfInterface())) {
          return;
        }
        if (parentElement.isTsInterface() && restatesInheritedMethods(parentElement)) {
          return;
        }
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

  /**
   * Whether this interface method would not extend a method it overrides, emitted in a supertype
   * TypeScript sees: it returns unknown where that doesn't, or one of them takes a rest parameter
   * where the other takes an array. Other parameter differences are fine, since TypeScript method
   * parameters are bivariant. The inherited declaration stands in its place. Warns with the fix.
   */
  // TODO a follow-up: where several such declarations disagree, leaving this out is still TS2320.
  private boolean isLeftOutOfInterface() {
    ExecutableElement method = (ExecutableElement) element;
    TypeElement declaring = (TypeElement) method.getEnclosingElement();
    HasProcessorEnv quiet = HasProcessorEnv.quiet(env);
    String ownReturn = TsElement.of(method, quiet).getType().emit("");
    String override = declaring.getSimpleName() + "." + method.getSimpleName() + "()";
    for (ExecutableElement superMethod : TsElement.of(declaring, env).overriddenMethods(method)) {
      if (!isEmittedInSupertype(superMethod)) {
        continue;
      }
      String inheritedReturn = TsElement.of(superMethod, declaring, quiet).getType().emit("");
      String overridden =
          superMethod.getEnclosingElement().getSimpleName()
              + "."
              + superMethod.getSimpleName()
              + "()";
      String fix;
      if (UNKNOWN.matcher(ownReturn).find() && !UNKNOWN.matcher(inheritedReturn).find()) {
        fix =
            override
                + " returns "
                + method.getReturnType()
                + ", which TypeScript sees as "
                + ownReturn
                + ", so it is left out and "
                + overridden
                + "'s declaration stands; return a type TypeScript can see, or annotate "
                + override
                + " with @TsTypeRef to choose the emitted type.";
      } else if (method.isVarArgs() != superMethod.isVarArgs()) {
        fix =
            override
                + " declares its parameters differently from "
                + overridden
                + " in TypeScript, so it is left out and "
                + overridden
                + "'s declaration stands; declare them as "
                + overridden
                + " does.";
      } else {
        continue;
      }
      env.messager().printMessage(Diagnostic.Kind.WARNING, fix, method);
      return true;
    }
    return false;
  }

  /**
   * Reports an override whose return type is wider in TypeScript than the one it overrides, which
   * TypeScript rejects: nullable where that isn't (nor any or unknown), or another type where that
   * is a string literal.
   */
  private void reportWidening() {
    ExecutableElement method = (ExecutableElement) element;
    TypeElement declaring = (TypeElement) method.getEnclosingElement();
    HasProcessorEnv quiet = HasProcessorEnv.quiet(env);
    TsType own = TsElement.of(method, quiet).getType();
    for (ExecutableElement overridden : TsElement.of(declaring, env).overriddenMethods(method)) {
      if (!isEmittedInSupertype(overridden)) {
        continue;
      }
      TsType inherited = TsElement.of(overridden, declaring, quiet).getType();
      String inheritedText = withoutNull(inherited.emit(""));
      String widening;
      String inheritedIs;
      // any and unknown already take null and undefined.
      if (own instanceof TsNullableType
          && !(inherited instanceof TsNullableType)
          && !"any".equals(inheritedText)
          && !"unknown".equals(inheritedText)) {
        widening = "is @JsNullable, but ";
        inheritedIs = "is not";
      } else if (STRING_LITERAL.matcher(inheritedText).matches()
          && !withoutNull(own.emit("")).equals(inheritedText)) {
        widening = "returns " + own.emit("") + ", but ";
        inheritedIs = "returns the @TsLiteral " + inheritedText;
      } else {
        continue;
      }
      String overriddenName =
          overridden.getEnclosingElement().getSimpleName()
              + "."
              + overridden.getSimpleName()
              + "()";
      env.messager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              declaring.getSimpleName()
                  + "."
                  + method.getSimpleName()
                  + "() "
                  + widening
                  + overriddenName
                  + ", which it overrides, "
                  + inheritedIs
                  + "; TypeScript rejects an override with a wider return type.",
              method);
      return;
    }
  }

  /** {@code emitted} without a trailing null or undefined, as a nullable type emits. */
  private static String withoutNull(String emitted) {
    return emitted.replaceFirst("(\\|null)?(\\|undefined)?$", "");
  }

  /** A blank comment, or one that only inherits, says nothing of its own. */
  private boolean hasOwnDocs(ExecutableElement method) {
    String comment = env.elements().getDocComment(method);
    return nonNull(comment) && !comment.replace("{@inheritDoc}", "").trim().isEmpty();
  }
}
