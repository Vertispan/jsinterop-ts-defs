/*
 * Copyright © 2026 Vertispan
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
package com.vertispan.tsdefs.impl.builders;

import com.vertispan.tsdefs.annotations.TsInterface;
import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.model.TsType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.tools.Diagnostic;
import jsinterop.annotations.JsType;

/**
 * What a subtype passes to the type parameters of its supertypes, following each extends and
 * implements clause up the hierarchy. Arguments are kept as written and converted where a member
 * using them is emitted, so their annotations survive and their diagnostics are reported only when
 * they reach the output, on the type whose clause writes them.
 */
public class InheritedTypeArguments {
  static final InheritedTypeArguments NONE = new InheritedTypeArguments(Collections.emptyMap());

  private Supplier<Map<Element, List<Binding>>> pending;
  private Map<Element, List<Binding>> bindings;

  private InheritedTypeArguments(Map<Element, List<Binding>> bindings) {
    this.bindings = bindings;
  }

  private InheritedTypeArguments(Supplier<Map<Element, List<Binding>>> pending) {
    this.pending = pending;
  }

  /**
   * The type arguments {@code subtype} passes up, directly or through other supertypes. The
   * hierarchy is walked on the first lookup.
   */
  public static InheritedTypeArguments of(TypeElement subtype, HasProcessorEnv env) {
    return new InheritedTypeArguments(
        () -> {
          Map<Element, List<Binding>> result = new HashMap<>();
          walk(subtype, NONE, new HashSet<>(), result, HasProcessorEnv.quiet(env));
          return result;
        });
  }

  /** {@code type}'s extends and implements clauses, raw or not. */
  private static List<DeclaredType> supertypeClauses(TypeElement type) {
    List<TypeMirror> supertypes = new ArrayList<>(type.getInterfaces());
    supertypes.add(type.getSuperclass());
    List<DeclaredType> clauses = new ArrayList<>();
    for (TypeMirror supertype : supertypes) {
      if (TypeKind.DECLARED.equals(supertype.getKind())) {
        clauses.add((DeclaredType) supertype);
      }
    }
    return clauses;
  }

  /** The clauses that pass type arguments: a raw one passes none. */
  private static List<DeclaredType> clauses(TypeElement type) {
    List<DeclaredType> clauses = supertypeClauses(type);
    clauses.removeIf(JavaToTsTypeConverter::isRaw);
    return clauses;
  }

  /**
   * Reports what {@code type}'s supertypes pass it that TypeScript can't take, for an emitted type.
   */
  public static void report(TypeElement type, HasProcessorEnv env) {
    reportRawHiddenSupertypes(type, env);
    reportConflicts(type, env);
  }

  /**
   * Reports raw clauses on the way up from {@code type} through supertypes that are not emitted
   * themselves, such as {@code @TsIgnore} types and skipped classes, whose members or clauses are
   * emitted into {@code type} instead. Each is reported on the type whose clause writes it, and
   * only if the raw type reaches the output.
   */
  private static void reportRawHiddenSupertypes(TypeElement type, HasProcessorEnv env) {
    reportRawClauses(type, supertypeClauses(type), env);
  }

  private static void reportRawClauses(
      TypeElement type, List<DeclaredType> clauses, HasProcessorEnv env) {
    for (DeclaredType clause : clauses) {
      TypeElement named = (TypeElement) clause.asElement();
      if (JavaToTsTypeConverter.isRaw(clause)) {
        String message = JavaToTsTypeConverter.rawTypeMessage(clause, type, true, env);
        if (reachesOutput(named, env) && env.firstReport(type, message)) {
          env.messager().printMessage(Diagnostic.Kind.ERROR, message, type);
        }
      } else if (TsElement.of(named, env).isTsIgnored() || !isSelectedJsType(named)) {
        reportRawClauses(named, carriedClauses(named), env);
      }
    }
  }

  /**
   * The clauses of {@code type} whose members or clauses are carried into its subtypes'
   * declarations: an interface's or a TsInterface's all, another class's only its superclass.
   */
  private static List<DeclaredType> carriedClauses(TypeElement type) {
    List<DeclaredType> clauses = supertypeClauses(type);
    if (!type.getKind().isInterface() && type.getAnnotation(TsInterface.class) == null) {
      clauses.removeIf(clause -> !isSuperclass(type, clause));
    }
    return clauses;
  }

  /** As the processor selects them: a native JsType is emitted only as a TsInterface. */
  private static boolean isSelectedJsType(TypeElement type) {
    JsType jsType = type.getAnnotation(JsType.class);
    return jsType != null && (!jsType.isNative() || type.getAnnotation(TsInterface.class) != null);
  }

  /** Whether {@code type}'s members or supertypes are emitted, in it or in its subtypes. */
  private static boolean reachesOutput(TypeElement type, HasProcessorEnv env) {
    TsElement tsElement = TsElement.of(type, env);
    return tsElement.isJsType()
        || tsElement.isTsInterface()
        || tsElement.hasJsMembers()
        || carriedClauses(type).stream()
            .anyMatch(clause -> reachesOutput((TypeElement) clause.asElement(), env));
  }

  private static boolean isSuperclass(TypeElement type, DeclaredType clause) {
    return clause.asElement().equals(((DeclaredType) type.getSuperclass()).asElement());
  }

  /**
   * Walks each supertype once per distinct list of arguments, so a lattice of diamonds is walked
   * once per type. A raw supertype passes nothing; it is reported where it is written.
   */
  private static void walk(
      TypeElement type,
      InheritedTypeArguments context,
      Set<List<Object>> walked,
      Map<Element, List<Binding>> result,
      HasProcessorEnv quietEnv) {
    for (DeclaredType clause : clauses(type)) {
      TypeElement superElement = (TypeElement) clause.asElement();
      List<? extends TypeParameterElement> parameters = superElement.getTypeParameters();
      Map<Element, List<Binding>> passed = new HashMap<>();
      List<Object> key = new ArrayList<>(Collections.singletonList(superElement));
      for (int i = 0; i < parameters.size(); i++) {
        Binding binding = new Binding(type, clause, i, context, quietEnv);
        passed.put(parameters.get(i), Collections.singletonList(binding));
        key.add(binding.emitted);
        List<Binding> known = result.computeIfAbsent(parameters.get(i), p -> new ArrayList<>());
        if (known.stream().noneMatch(other -> other.emitted.equals(binding.emitted))) {
          known.add(binding);
        }
      }
      if (walked.add(key)) {
        walk(superElement, new InheritedTypeArguments(passed), walked, result, quietEnv);
      }
    }
  }

  private Map<Element, List<Binding>> bindings() {
    if (bindings == null) {
      bindings = pending.get();
      pending = null;
    }
    return bindings;
  }

  /**
   * {@code typeParameter} as passed down to the subtype, or empty if it is not passed. Of arguments
   * that differ through different paths, the first; {@link #reportConflicts} reports them.
   */
  Optional<TsType> lookup(Element typeParameter, HasProcessorEnv env) {
    List<Binding> passed = bindings().get(typeParameter);
    if (passed == null || passed.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(passed.get(0).convert(env));
  }

  /**
   * Whether {@code typeParameter} is reached with arguments that are different TypeScript types,
   * and its type mentions it anywhere, so that a declaration could disagree.
   */
  private boolean conflicts(Element typeParameter, HasProcessorEnv env) {
    List<Binding> passed = bindings().getOrDefault(typeParameter, Collections.emptyList());
    return passed.stream().map(binding -> sameType(binding.emitted)).distinct().count() > 1
        && mentionsAnywhere(typeParameter, env);
  }

  /** {@code any} and {@code unknown} already include null and undefined. */
  private static String sameType(String emitted) {
    return emitted.matches("(any|unknown)(\\|null)?(\\|undefined)?")
        ? emitted.replaceAll("\\|.*", "")
        : emitted;
  }

  /** In an emitted member of the type declaring it, or in a clause passing it on. */
  private static boolean mentionsAnywhere(Element typeParameter, HasProcessorEnv env) {
    TypeElement owner = (TypeElement) typeParameter.getEnclosingElement();
    // Overlays and ignored members never reach TypeScript.
    return owner.getEnclosedElements().stream()
            .filter(member -> TsElement.of(member, env).isExportable())
            .anyMatch(member -> mentions(member.asType(), typeParameter))
        || supertypeClauses(owner).stream().anyMatch(clause -> mentions(clause, typeParameter));
  }

  private static boolean mentions(TypeMirror type, Element typeParameter) {
    switch (type.getKind()) {
      case TYPEVAR:
        // Not its bound, which can mention itself; bounds are checked where they are declared.
        return ((TypeVariable) type).asElement().equals(typeParameter);
      case DECLARED:
        return ((DeclaredType) type)
            .getTypeArguments().stream().anyMatch(argument -> mentions(argument, typeParameter));
      case ARRAY:
        return mentions(((ArrayType) type).getComponentType(), typeParameter);
      case WILDCARD:
        WildcardType wildcard = (WildcardType) type;
        return (wildcard.getExtendsBound() != null
                && mentions(wildcard.getExtendsBound(), typeParameter))
            || (wildcard.getSuperBound() != null
                && mentions(wildcard.getSuperBound(), typeParameter));
      case EXECUTABLE:
        ExecutableType method = (ExecutableType) type;
        return mentions(method.getReturnType(), typeParameter)
            || method.getParameterTypes().stream().anyMatch(p -> mentions(p, typeParameter))
            || method.getTypeVariables().stream()
                .anyMatch(v -> mentions(v.getUpperBound(), typeParameter));
      case INTERSECTION:
        return ((IntersectionType) type)
            .getBounds().stream().anyMatch(bound -> mentions(bound, typeParameter));
      default:
        return false;
    }
  }

  /**
   * Reports each type parameter {@code subtype} reaches with arguments that differ in TypeScript,
   * e.g. only in nullability, where its type mentions it at all: declarations could disagree on
   * which one TypeScript sees. Once, on the type where the paths meet.
   */
  private static void reportConflicts(TypeElement subtype, HasProcessorEnv env) {
    InheritedTypeArguments passed = of(subtype, env);
    passed.bindings().keySet().stream()
        .filter(typeParameter -> passed.conflicts(typeParameter, env))
        .forEach(
            typeParameter ->
                reportConflict(meeting(subtype, typeParameter, env), typeParameter, env));
  }

  /** Below the meeting point a supertype has the conflict already, so it is reported there. */
  private static TypeElement meeting(TypeElement type, Element typeParameter, HasProcessorEnv env) {
    for (DeclaredType clause : clauses(type)) {
      TypeElement supertype = (TypeElement) clause.asElement();
      if (of(supertype, env).conflicts(typeParameter, env)) {
        return meeting(supertype, typeParameter, env);
      }
    }
    return type;
  }

  private static String path(Binding binding, TypeElement meeting) {
    return binding.through.equals(meeting)
        ? "in its own clause"
        : "through " + binding.through.getSimpleName();
  }

  private static void reportConflict(
      TypeElement meeting, Element typeParameter, HasProcessorEnv env) {
    List<Binding> passed = of(meeting, env).bindings().get(typeParameter);
    Binding first = passed.get(0);
    Binding other =
        passed.stream()
            .filter(binding -> !sameType(binding.emitted).equals(sameType(first.emitted)))
            .findFirst()
            .get();
    String message =
        "Type parameter "
            + typeParameter.getSimpleName()
            + " of "
            + typeParameter.getEnclosingElement().getSimpleName()
            + " is passed ["
            + first.emitted
            + "] "
            + path(first, meeting)
            + " but ["
            + other.emitted
            + "] "
            + path(other, meeting)
            + " in "
            + meeting.getSimpleName()
            + "; the type arguments disagree. Make them agree.";
    if (env.firstReport(meeting, message)) {
      env.messager().printMessage(Diagnostic.Kind.ERROR, message, meeting);
    }
  }

  /** One argument as written in a clause of {@code through}, with what reached {@code through}. */
  private static class Binding {
    private final TypeElement through;
    private final DeclaredType clause;
    private final int index;
    private final InheritedTypeArguments context;
    /** As emitted, to compare arguments reaching one parameter through different paths. */
    private final String emitted;

    private Binding(
        TypeElement through,
        DeclaredType clause,
        int index,
        InheritedTypeArguments context,
        HasProcessorEnv quietEnv) {
      this.through = through;
      this.clause = clause;
      this.index = index;
      this.context = context;
      this.emitted = convert(quietEnv).emit("");
    }

    private TsType convert(HasProcessorEnv env) {
      return JavaToTsTypeConverter.forSupertypesOf(through, context, env)
          .typeArgument(clause.getTypeArguments().get(index));
    }
  }
}
