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
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * What a subtype passes to the type parameters of its supertypes, following each extends and
 * implements clause up the hierarchy. Arguments are kept as written and converted where a member
 * using them is emitted, so their annotations survive and their diagnostics are reported only when
 * they reach the output, on the type whose clause writes them.
 */
public class InheritedTypeArguments {
  static final InheritedTypeArguments NONE = new InheritedTypeArguments(Collections.emptyMap());

  private Supplier<Map<Element, List<Binding>>> walk;
  private Map<Element, List<Binding>> bindings;

  private InheritedTypeArguments(Map<Element, List<Binding>> bindings) {
    this.bindings = bindings;
  }

  private InheritedTypeArguments(Supplier<Map<Element, List<Binding>>> walk) {
    this.walk = walk;
  }

  /**
   * The type arguments {@code subtype} passes up, directly or through other supertypes. The
   * hierarchy is walked on the first lookup.
   */
  public static InheritedTypeArguments of(TypeElement subtype, HasProcessorEnv env) {
    return new InheritedTypeArguments(
        () -> {
          Map<Element, List<Binding>> result = new HashMap<>();
          walk(
              subtype,
              NONE,
              Collections.singletonList(subtype),
              new HashSet<>(),
              result,
              HasProcessorEnv.quiet(env));
          return result;
        });
  }

  /**
   * Walks each supertype once per distinct list of arguments, so a lattice of diamonds is walked
   * once per type. A raw supertype passes nothing; it is reported where it is written.
   */
  private static void walk(
      TypeElement type,
      InheritedTypeArguments context,
      List<TypeElement> path,
      Set<List<Object>> walked,
      Map<Element, List<Binding>> result,
      HasProcessorEnv quietEnv) {
    List<TypeMirror> supertypes = new ArrayList<>(type.getInterfaces());
    supertypes.add(type.getSuperclass());
    for (TypeMirror supertype : supertypes) {
      if (!TypeKind.DECLARED.equals(supertype.getKind())
          || JavaToTsTypeConverter.isRaw((DeclaredType) supertype)) {
        continue;
      }
      DeclaredType clause = (DeclaredType) supertype;
      TypeElement superElement = (TypeElement) clause.asElement();
      List<? extends TypeParameterElement> parameters = superElement.getTypeParameters();
      Map<Element, List<Binding>> passed = new HashMap<>();
      List<Object> key = new ArrayList<>(Collections.singletonList(superElement));
      for (int i = 0; i < parameters.size(); i++) {
        Binding binding = new Binding(path, clause, i, context, quietEnv);
        passed.put(parameters.get(i), Collections.singletonList(binding));
        key.add(binding.emitted);
        List<Binding> known = result.computeIfAbsent(parameters.get(i), p -> new ArrayList<>());
        if (known.stream().noneMatch(other -> other.emitted.equals(binding.emitted))) {
          known.add(binding);
        }
      }
      if (walked.add(key)) {
        List<TypeElement> superPath = new ArrayList<>(path);
        superPath.add(superElement);
        walk(superElement, new InheritedTypeArguments(passed), superPath, walked, result, quietEnv);
      }
    }
  }

  private Map<Element, List<Binding>> bindings() {
    if (bindings == null) {
      bindings = walk.get();
      walk = null;
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
   * Reports each type parameter reached with arguments that differ in TypeScript, e.g. only in
   * nullability, as TypeScript has no one type for both. Once, on the type where the paths meet,
   * whether or not an emitted member uses the parameter.
   */
  public void reportConflicts(HasProcessorEnv env) {
    bindings()
        .forEach(
            (typeParameter, passed) -> {
              if (passed.size() > 1) {
                reportConflict(typeParameter, passed.get(0), passed.get(1), env);
              }
            });
  }

  private static void reportConflict(
      Element typeParameter, Binding first, Binding other, HasProcessorEnv env) {
    TypeElement meeting = first.path.get(0);
    for (int i = 0;
        i < Math.min(first.path.size(), other.path.size())
            && first.path.get(i).equals(other.path.get(i));
        i++) {
      meeting = first.path.get(i);
    }
    if (!env.firstReport(meeting, typeParameter)) {
      return;
    }
    env.messager()
        .printMessage(
            Diagnostic.Kind.ERROR,
            "Type parameter "
                + typeParameter.getSimpleName()
                + " of "
                + typeParameter.getEnclosingElement().getSimpleName()
                + " is passed ["
                + first.emitted
                + "] through "
                + first.through().getSimpleName()
                + " but ["
                + other.emitted
                + "] through "
                + other.through().getSimpleName()
                + " in "
                + meeting.getSimpleName()
                + "; TypeScript has no one type for both. Make the type arguments agree.",
            meeting);
  }

  /**
   * One argument as written in a clause of the last type on {@code path}, with what reached that
   * type.
   */
  private static class Binding {
    /** From the subtype to the type whose clause this is. */
    private final List<TypeElement> path;

    private final DeclaredType clause;
    private final int index;
    private final InheritedTypeArguments context;
    /** As emitted, to compare arguments reaching one parameter through different paths. */
    private final String emitted;

    private Binding(
        List<TypeElement> path,
        DeclaredType clause,
        int index,
        InheritedTypeArguments context,
        HasProcessorEnv quietEnv) {
      this.path = path;
      this.clause = clause;
      this.index = index;
      this.context = context;
      this.emitted = convert(quietEnv).emit("");
    }

    private TypeElement through() {
      return path.get(path.size() - 1);
    }

    private TsType convert(HasProcessorEnv env) {
      return JavaToTsTypeConverter.forSupertypesOf(through(), context, env)
          .clauseArgument(clause.getTypeArguments().get(index));
    }
  }
}
