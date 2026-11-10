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
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;

public class ClassMethodVisitor<T> extends TsElement {
  /** A string, number or boolean literal type, as emitted. */
  private static final Pattern LITERAL = Pattern.compile("\".*\"|-?[0-9.]+|true|false");

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

    if (isMethod() && inheritedBy == null) {
      warnIfNarrowingIsLost();
    }
    if (isMethod() && !isJsProperty() && isExportable() && !isGetter() && !isSetter()) {

      // A type's own declaration only: a copy is emitted next to the inherited member it overrides,
      // which is copied too. TODO a follow-up copies only the most specific override.
      if (inheritedBy == null) {
        reportWidening();
        TsElement parentElement = TsElement.of(element.getEnclosingElement(), env);
        // An interface inherits the wider declaration; a class that implements one inherits
        // nothing, and one left without members isn't emitted.
        if ((parentElement.isInterface() || parentElement.isTsInterface())
            && narrowsBeyondTypeScript()) {
          return;
        }
        if (parentElement.isTsInterface()
            && (restatesInheritedMethods(parentElement) || isExportedUnderAnotherName())) {
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
   * Whether JsInterop exports this unannotated override under the JS name of an exported method it
   * overrides, which differs from its own; this emitter would name it wrongly.
   */
  private boolean isExportedUnderAnotherName() {
    ExecutableElement method = (ExecutableElement) element;
    if (nonNull(method.getAnnotation(JsMethod.class))
        || nonNull(method.getAnnotation(JsProperty.class))) {
      return false;
    }
    return TsElement.of(method.getEnclosingElement(), env).overriddenMethods(method).stream()
        .map(superMethod -> TsElement.of(superMethod, env))
        .anyMatch(
            overridden -> overridden.isExportable() && !overridden.getName().equals(getName()));
  }

  /**
   * JsInterop exports an unannotated override of a renamed method or a property accessor under the
   * overridden member's JS name, which this emitter doesn't model: TypeScript sees only the
   * inherited declaration, so a narrower return type is lost. Warns with the annotation that emits
   * it.
   */
  private void warnIfNarrowingIsLost() {
    ExecutableElement method = (ExecutableElement) element;
    TsElement declaring = TsElement.of(method.getEnclosingElement(), env);
    // A TsInterface leaves such an override out (isExportedUnderAnotherName); elsewhere an
    // exported one is emitted under its Java name. TODO a follow-up emits it under the JS name.
    if (nonNull(method.getAnnotation(JsMethod.class))
        || nonNull(method.getAnnotation(JsProperty.class))
        || (isExportable() && !declaring.isTsInterface())) {
      return;
    }
    DeclaredType declaringType = (DeclaredType) declaring.element().asType();
    List<ExecutableElement> overriddenMethods = declaring.overriddenMethods(method);
    // Nothing is lost when a supertype in between already emits the narrower type.
    if (overriddenMethods.stream()
        .anyMatch(
            superMethod ->
                TsElement.of(superMethod, env).isExportable()
                    && env.types()
                        .isSameType(
                            method.getReturnType(),
                            ((ExecutableType) env.types().asMemberOf(declaringType, superMethod))
                                .getReturnType()))) {
      return;
    }
    for (ExecutableElement superMethod : overriddenMethods) {
      TsElement overridden = TsElement.of(superMethod, env);
      if (!overridden.isExportable() || overridden.getName().equals(getName())) {
        continue;
      }
      TypeMirror inheritedReturn =
          ((ExecutableType) env.types().asMemberOf(declaringType, superMethod)).getReturnType();
      if (env.types().isSameType(method.getReturnType(), inheritedReturn)) {
        continue;
      }
      boolean property = overridden.isJsProperty();
      String jsName = property ? overridden.nonGetSetName() : overridden.getName();
      String override = declaring.element().getSimpleName() + "." + method.getSimpleName() + "()";
      String inherited =
          superMethod.getEnclosingElement().getSimpleName()
              + "."
              + superMethod.getSimpleName()
              + "()";
      env.messager()
          .printMessage(
              Diagnostic.Kind.WARNING,
              override
                  + " narrows the return type of "
                  + inherited
                  + " to "
                  + method.getReturnType()
                  + ", but is exported under "
                  + inherited
                  + "'s JS name, which isn't modeled, so the declaration keeps "
                  + inherited
                  + "'s type; annotate "
                  + override
                  + " with "
                  + (property ? "@JsProperty" : "@JsMethod")
                  + "(name = \""
                  + jsName
                  + "\") to emit the narrower type.",
              method);
      return;
    }
  }

  /**
   * Reports an override whose return type is wider in TypeScript than the one it overrides, which
   * TypeScript rejects: nullable where that isn't, or the full type where that is a literal.
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
      String widening;
      if (own instanceof TsNullableType && !(inherited instanceof TsNullableType)) {
        widening = "is @JsNullable, but ";
      } else if (LITERAL.matcher(inherited.emit("")).matches()
          && !own.emit("").equals(inherited.emit(""))) {
        widening = "returns " + own.emit("") + ", but ";
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
                  + (own instanceof TsNullableType
                      ? "is not"
                      : "returns the @TsLiteral " + inherited.emit(""))
                  + "; TypeScript rejects an override with a wider return type.",
              method);
      return;
    }
  }

  /** A blank comment, or one that only inherits, says nothing of its own. */
  private boolean hasOwnDocs(ExecutableElement method) {
    String comment = env.elements().getDocComment(method);
    return nonNull(comment) && !comment.replace("{@inheritDoc}", "").trim().isEmpty();
  }
}
