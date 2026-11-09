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

import static java.util.Objects.nonNull;

import com.vertispan.tsdefs.annotations.TsReadOnly;
import com.vertispan.tsdefs.annotations.TsTypeDef;
import com.vertispan.tsdefs.annotations.TsTypeRef;
import com.vertispan.tsdefs.impl.Formatting;
import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.model.*;
import com.vertispan.tsdefs.impl.visitors.ParameterVisitor;
import com.vertispan.tsdefs.impl.visitors.TypeArgumentsVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.tools.Diagnostic;
import jsinterop.base.JsPropertyMap;

public class JavaToTsTypeConverter {
  private final HasProcessorEnv env;
  private final Element element;
  private final String emissionContext;
  private final Map<Element, TsType> typeArguments;
  private final Element rawTypeElement;

  public JavaToTsTypeConverter(Element element, HasProcessorEnv env) {
    this(element, "", Collections.emptyMap(), env);
  }

  /**
   * @param typeArguments what each type parameter is emitted as, keyed by its {@link
   *     TypeParameterElement}; type variables not in it are emitted by name
   */
  public JavaToTsTypeConverter(
      Element element, Map<Element, TsType> typeArguments, HasProcessorEnv env) {
    this(element, "", typeArguments, env);
  }

  /**
   * @param inheritedBy the subtype whose TypeScript declaration {@code element} is being emitted
   *     into, when that is not the type that declares it; inherited members are emitted once per
   *     subtype
   */
  public JavaToTsTypeConverter(Element element, TypeElement inheritedBy, HasProcessorEnv env) {
    this(element, inheritedSuffix(element, inheritedBy), Collections.emptyMap(), env);
  }

  /** Converts the type arguments in {@code subtype}'s extends and implements clauses. */
  public static JavaToTsTypeConverter forSupertypesOf(TypeElement subtype, HasProcessorEnv env) {
    return new JavaToTsTypeConverter(
        subtype, " in the supertypes of " + subtype.getSimpleName(), Collections.emptyMap(), env);
  }

  /**
   * @param emissionContext appended to diagnostics about {@code element}'s types when they are
   *     emitted somewhere other than its own declaration, or empty
   */
  private JavaToTsTypeConverter(
      Element element,
      String emissionContext,
      Map<Element, TsType> typeArguments,
      HasProcessorEnv env) {
    this(element, emissionContext, typeArguments, element, env);
  }

  /**
   * @param rawTypeElement where a raw type is reported: where it is written, which for a
   *     JsFunction's return type or a union member's type is that method rather than {@code
   *     element}
   */
  private JavaToTsTypeConverter(
      Element element,
      String emissionContext,
      Map<Element, TsType> typeArguments,
      Element rawTypeElement,
      HasProcessorEnv env) {
    this.env = env;
    this.element = element;
    this.emissionContext = emissionContext;
    this.typeArguments = typeArguments;
    this.rawTypeElement = rawTypeElement;
  }

  private static String inheritedSuffix(Element element, TypeElement inheritedBy) {
    if (!nonNull(inheritedBy) || inheritedBy.equals(element.getEnclosingElement())) {
      return "";
    }
    return " in "
        + inheritedBy.getSimpleName()
        + " (inherited from "
        + element.getEnclosingElement().getSimpleName()
        + ")";
  }

  public TsType toTsType(TypeMirror type) {
    return toTsType(type, false);
  }

  public TsType toTsType(TypeMirror type, boolean checkTypeDef) {
    TsType tsType = getTsType(type, checkTypeDef);
    if (!tsType.isTsReadOnly()) {
      tsType.setTsReadOnly(isTsReadOnlyType(type));
    }
    return tsType;
  }

  private TsType getTsType(TypeMirror type, boolean checkTypeDef) {
    if (TypeKind.TYPEVAR.equals(type.getKind())
        && typeArguments.containsKey(((TypeVariable) type).asElement())) {
      // A copy: callers set flags such as read-only on the result, per use.
      return typeArguments.get(((TypeVariable) type).asElement()).copy();
    }
    if (TypeKind.WILDCARD.equals(type.getKind())) {
      return wildcardType((WildcardType) type);
    }
    if (checkTypeDef && TsElement.of(type, env).isTsTypeDef()) {
      Optional<TsType> tsType = fromTsTypeDef(TsElement.of(type, env).element);
      if (tsType.isPresent()) {
        return tsType.get();
      }
    }
    if (type.getKind().isPrimitive()) {
      switch (type.getKind()) {
        case BOOLEAN:
          return TsType.of("boolean");
        case DOUBLE:
        case INT:
          return TsType.of("number");
        case VOID:
          return TsType.of("void");
        default:
          return TsType.of("unknown");
      }
    } else {
      if (isSameType(type, String.class)) {
        return TsType.of("string");
      }
      if (isSameType(type, Double.class)) {
        return TsType.of("number");
      }
      if (isSameType(type, Boolean.class)) {
        return TsType.of("boolean");
      }

      if (isSameType(type, Void.class) || "void".equals(type.toString())) {
        return TsType.of("void");
      }

      if (Object.class.getCanonicalName().equals(type.toString())) {
        return TsType.of("object");
      }

      // A read-only inner array is converted as an element of the outer one.
      if (ProcessorType.of(type, env).is2dArray()
          && !isTsReadOnlyType(ProcessorType.of(type, env).arrayComponentType())) {
        TypeMirror arrayComponentType = ProcessorType.of(type, env).deepArrayComponentType();
        TsType componentTsType = toTsType(arrayComponentType);
        if (needsArrayOf(arrayComponentType, componentTsType)) {
          return TsElement.of(arrayComponentType, env)
              .typeOrNullable(
                  ParameterizedTsType.of(
                      "Array", "", ParameterizedTsType.of("Array", "", componentTsType)));
        } else {
          return TsElement.of(arrayComponentType, env)
              .typeOrNullable(Array2dTsType.of(componentTsType));
        }
      }

      if (ProcessorType.of(type, env).isArray()) {
        TypeMirror arrayComponentType = ProcessorType.of(type, env).arrayComponentType();
        TsType componentTsType = toTsType(arrayComponentType);
        if (needsArrayOf(arrayComponentType, componentTsType)) {
          return TsElement.of(arrayComponentType, env)
              .typeOrNullable(ParameterizedTsType.of("Array", "", componentTsType));
        } else {
          ArrayTsType arrayType = ArrayTsType.of(componentTsType);
          arrayType.setTsReadOnly(
              TsElement.of(element, env).isTsReadOnly() || isTsReadOnlyType(element.asType()));
          return TsElement.of(arrayComponentType, env).typeOrNullable(arrayType);
        }
      }

      if (ProcessorType.of(type, env).isAssignableFrom(JsPropertyMap.class)) {
        DeclaredType declaredType = (DeclaredType) type;
        if (isRaw(declaredType)) {
          return reportRawType(declaredType);
        }
        // The index signature reads and writes, so every member counts as emitted.
        warnIfSuperWildcardIsProduced(declaredType, 0, member -> true);
        return TsTemplatedInlinedType.of(
            "{ [key: string]: $T; }", asTypeWithArgument(declaredType).get(0));
      }

      if (ProcessorType.of(type, env).isJsFunction()) {
        if (isRaw((DeclaredType) type)) {
          return reportRawType((DeclaredType) type);
        }
        return inlineJsFunctionType(type);
      }

      if (ProcessorType.of(type, env).isUnionType()) {
        if (TsElement.of(type, env).isExplicitUnionType()) {
          return getDeclaredType((DeclaredType) type, TsElement.of(type, env));
        }
        // Before expanding it: a raw union's member types are erased, and would be reported.
        if (isRaw((DeclaredType) type)) {
          return reportRawType((DeclaredType) type);
        }
        return unionType(type);
      }

      return getReferenceType(type);
    }
  }

  /**
   * Whether the element must be written {@code Array<...>}: a bare {@code []} would bind to part of
   * it, e.g. to a function's return type or to the inner array of {@code readonly string[]}. A type
   * variable can be mapped to a union or nullable, so the converted element is checked too.
   */
  private boolean needsArrayOf(TypeMirror component, TsType componentTsType) {
    return ProcessorType.of(component, env).isUnionType()
        || componentTsType instanceof TsUnionType
        || componentTsType instanceof TsNullableType
        || componentTsType instanceof TsInlinedFunctionType
        || (componentTsType instanceof ArrayTsType && componentTsType.isTsReadOnly());
  }

  /** Whether {@code type} omits the type arguments its declaration requires. */
  public static boolean isRaw(DeclaredType type) {
    return type.getTypeArguments().isEmpty()
        && !((TypeElement) type.asElement()).getTypeParameters().isEmpty();
  }

  /**
   * Names where the raw type is written, {@code where} being the element it is reported on, since
   * the diagnostic's position can be missing (javadoc on parameters, library classes). Suggests
   * only arguments Java accepts in that position: {@code Any} when no type parameter is bounded, a
   * wildcard when not in an extends or implements clause.
   */
  public static String rawTypeMessage(
      DeclaredType type, Element where, boolean inSupertypeClause, HasProcessorEnv env) {
    String name = typeName((TypeElement) type.asElement());
    List<? extends TypeParameterElement> parameters =
        ((TypeElement) type.asElement()).getTypeParameters();
    List<String> examples = new ArrayList<>();
    boolean bounded =
        parameters.stream()
            .flatMap(parameter -> parameter.getBounds().stream())
            .anyMatch(bound -> !isSameType(bound, Object.class, env));
    if (!bounded) {
      examples.add(
          name
              + "<"
              + String.join(", ", Collections.nCopies(parameters.size(), "Any"))
              + "> (jsinterop.base.Any)");
    }
    if (!inSupertypeClause) {
      examples.add(
          name + "<" + String.join(", ", Collections.nCopies(parameters.size(), "?")) + ">");
    }
    return "Raw type "
        + name
        + " "
        + location(where)
        + " is not supported; give its type arguments"
        + (examples.isEmpty() ? "" : ", e.g. " + String.join(" or ", examples))
        + ".";
  }

  private static String location(Element where) {
    if (where instanceof TypeElement) {
      return "in the supertypes of " + typeName((TypeElement) where);
    }
    switch (where.getKind()) {
      case PARAMETER:
        return "in parameter "
            + where.getSimpleName()
            + " of "
            + memberName(where.getEnclosingElement());
      case TYPE_PARAMETER:
        return "in the bound of type parameter "
            + where.getSimpleName()
            + " of "
            + memberName(((TypeParameterElement) where).getGenericElement());
      default:
        return "in " + memberName(where);
    }
  }

  private static String memberName(Element member) {
    if (member instanceof TypeElement) {
      return typeName((TypeElement) member);
    }
    String type = typeName((TypeElement) member.getEnclosingElement());
    return ElementKind.CONSTRUCTOR.equals(member.getKind())
        ? "the " + type + " constructor"
        : type + "." + member.getSimpleName();
  }

  /** With enclosing types, as written in Java: Outer.Inner. */
  private static String typeName(TypeElement type) {
    String name = type.getSimpleName().toString();
    for (Element enclosing = type.getEnclosingElement();
        enclosing instanceof TypeElement;
        enclosing = enclosing.getEnclosingElement()) {
      name = enclosing.getSimpleName() + "." + name;
    }
    return name;
  }

  private TsType reportRawType(DeclaredType type) {
    if (env.firstReport(rawTypeElement, type.asElement())) {
      env.messager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              rawTypeMessage(type, rawTypeElement, false, env),
              rawTypeElement);
    }
    return TsType.of("any");
  }

  /**
   * TypeScript has no wildcards and does not need them: generics are structurally variant there, so
   * a Java {@code ? extends X} or {@code ? super X} is rendered as the bound {@code X}, which is
   * the type a TypeScript author would have written by hand. An unbounded {@code ?} renders as
   * {@code any}, consistent with how other unresolvable types are emitted. The bound is treated
   * like any other type argument, so {@code @TsTypeRef} and {@code @JsNullable} apply to it whether
   * written on the wildcard or on the bound.
   */
  private TsType wildcardType(WildcardType wildcard) {
    TypeMirror bound =
        nonNull(wildcard.getExtendsBound()) ? wildcard.getExtendsBound() : wildcard.getSuperBound();
    if (nonNull(bound) && !isSameType(bound, Object.class)) {
      return typeArgument(bound);
    }
    return TsType.of(Formatting.resolveName("?"));
  }

  public TsType unionType(TypeMirror type) {
    return TsUnionType.of(true, unionTypes((DeclaredType) type));
  }

  public TsType unionType(Element element) {
    DeclaredType declaredType = (DeclaredType) element.asType();
    TsElement tsElement = TsElement.of(element, env);
    return TsUnionType.of(
        tsElement.getExplicitUnionName(),
        tsElement.getExplicitUnionNamespace(),
        true,
        TypeArgumentsVisitor.declaredTypeParameters(declaredType, env),
        unionTypes(declaredType));
  }

  private Set<TsType> unionTypes(DeclaredType declaredType) {
    Element element = declaredType.asElement();
    return element.getEnclosedElements().stream()
        .map(e -> TsElement.of(e, env))
        .filter(TsElement::isUnionMember)
        .map(
            e -> {
              if (e.isMethod()) {
                // A raw type in the member is reported on the member, where it is written.
                return e.typeOrNullable(
                    new JavaToTsTypeConverter(
                            this.element, emissionContext, typeArguments, e.element(), env)
                        .toTsType(
                            ((ExecutableType) env.types().asMemberOf(declaredType, e.element()))
                                .getReturnType()));
              }
              return e.getType();
            })
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private TsType inlineJsFunctionType(TypeMirror type) {
    Optional<TsElement> first =
        TsElement.of(type, env).element().getEnclosedElements().stream()
            .filter(element -> ElementKind.METHOD.equals(element.getKind()))
            .map(element -> TsElement.of(element, env))
            .filter(tsElement -> !tsElement.isOverlay() && !tsElement.isIgnored())
            .findFirst();

    ExecutableElement executableElement = (ExecutableElement) first.get().element();
    if (!executableElement.getTypeParameters().isEmpty()) {
      // Unsupported; TypeVisitor reports it on the JsFunction's declaration.
      return TsType.of("any");
    }
    TsInlinedFunctionType tsType = new TsInlinedFunctionType();
    List<? extends TypeMirror> declaredArguments = ((DeclaredType) type).getTypeArguments();
    List<? extends TypeMirror> argumentVars =
        ((DeclaredType) executableElement.getEnclosingElement().asType()).getTypeArguments();
    Map<Element, TsType> typeMapping = new HashMap<>();
    for (int index = 0; index < argumentVars.size(); index++) {
      // Only the function method is emitted, as the inlined signature.
      warnIfSuperWildcardIsProduced(
          (DeclaredType) type, index, member -> member.equals(executableElement));
      typeMapping.put(
          ((TypeVariable) argumentVars.get(index)).asElement(),
          typeArgument(declaredArguments.get(index)));
    }
    executableElement
        .getParameters()
        .forEach(
            param ->
                new ParameterVisitor<TsInlinedFunctionType>(param, typeMapping, env).visit(tsType));

    tsType.setReturnType(
        new JavaToTsTypeConverter(element, emissionContext, typeMapping, executableElement, env)
            .toTsType(executableElement.getReturnType()));
    return tsType;
  }

  private TsType getReferenceType(TypeMirror type) {
    if (TypeKind.TYPEVAR.equals(type.getKind())) {
      // By name: toString() includes type-use annotations.
      return TsType.of(((TypeVariable) type).asElement().getSimpleName().toString(), "");
    } else if (TypeKind.TYPEVAR.equals(type.getKind())) {
      return TsType.of("any");
    } else if (TypeKind.DECLARED.equals(type.getKind())) {
      DeclaredType declaredType = (DeclaredType) type;
      if (TsElement.of(declaredType, env).isTsTypeRef()) {
        return getTsTypeReference(declaredType);
      } else if (!TsElement.of(declaredType, env).isPublic()) {
        env.messager()
            .printMessage(
                Diagnostic.Kind.ERROR,
                "A none public type ["
                    + declaredType.asElement().getSimpleName()
                    + "] is referenced but it will not be exported.",
                element);
      } else {
        TsElement typeElement = new TsElement(declaredType.asElement(), env);
        if (typeElement.isJsType() || typeElement.isJsFunction() || typeElement.hasJsMembers()) {
          return getDeclaredType(declaredType, typeElement);
        }
      }
    }
    return TsType.of(Formatting.resolveNameOr(type.toString(), "unknown"));
  }

  private TsType getDeclaredType(DeclaredType declaredType, TsElement typeElement) {
    if (isRaw(declaredType)) {
      return reportRawType(declaredType);
    }
    boolean explicitUnion = typeElement.isExplicitUnionType();
    String name = explicitUnion ? typeElement.getExplicitUnionName() : typeElement.getName();
    String nameSpace =
        explicitUnion
            ? typeElement.getExplicitUnionNamespace()
            : typeElement.isJsFunction() ? "" : typeElement.getNamespace();

    if (!declaredType.getTypeArguments().isEmpty()) {
      return ParameterizedTsType.of(name, nameSpace, asTypeWithArgument(declaredType));
    }
    return TsType.of(name, nameSpace);
  }

  private TsType getTsTypeReference(DeclaredType declaredType) {
    if (!TsElement.of(declaredType, env).isPublic()) {
      env.messager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              "A none public type ["
                  + declaredType.asElement().getSimpleName()
                  + "] is referenced but it will not be exported.",
              element);
    }

    Optional<TypeMirror> typeRef =
        TsElement.of(declaredType.asElement(), env)
            .getClassValueFromAnnotation(TsTypeRef.class, "value");
    return toTsType(typeRef.get());
  }

  /** Converts the type arguments of a parameterized type as used in a type reference. */
  public List<TsType> asTypeWithArgument(DeclaredType type) {
    List<? extends TypeMirror> arguments = type.getTypeArguments();
    List<TsType> result = new ArrayList<>(arguments.size());
    for (int i = 0; i < arguments.size(); i++) {
      warnIfSuperWildcardIsProduced(type, i);
      result.add(typeArgument(arguments.get(i)));
    }
    return result;
  }

  /**
   * Erasing {@code ? super X} to {@code X} is exact when the parameterized type only consumes its
   * parameter. When it also produces it, through a method return type or a field, TypeScript
   * callers are promised an {@code X} but may receive any supertype of it, so warn and point at
   * {@code @TsTypeRef} as the way to choose the emitted type. Only the member being emitted is
   * considered: a wildcard in a parameter position or in a type parameter's bound makes the
   * TypeScript signature stricter, not wrong, so it is left alone, and variance of nested type
   * arguments is not tracked. A member whose type, or the part of it naming the parameter, is
   * replaced by {@code @TsTypeRef} does not produce the parameter in TypeScript.
   */
  private void warnIfSuperWildcardIsProduced(DeclaredType type, int index) {
    warnIfSuperWildcardIsProduced(type, index, member -> TsElement.of(member, env).isExportable());
  }

  /**
   * @param emitted which members of the parameterized type reach TypeScript; the default is {@link
   *     TsElement#isExportable()}, but inlined types such as JsFunctions and JsPropertyMap emit
   *     members that are not exportable on their own
   */
  private void warnIfSuperWildcardIsProduced(
      DeclaredType type, int index, Predicate<Element> emitted) {
    TypeMirror argument = type.getTypeArguments().get(index);
    if (!TypeKind.WILDCARD.equals(argument.getKind())
        || ElementKind.PARAMETER.equals(element.getKind())
        || ElementKind.TYPE_PARAMETER.equals(element.getKind())
        || getTypeOrTypeRef(argument) != argument) {
      return;
    }
    TypeMirror bound = ((WildcardType) argument).getSuperBound();
    TypeElement typeElement = (TypeElement) type.asElement();
    List<? extends TypeParameterElement> parameters = typeElement.getTypeParameters();
    if (!nonNull(bound) || getTypeOrTypeRef(bound) != bound) {
      return;
    }
    TypeMirror parameter = parameters.get(index).asType();
    DeclaredType generic = (DeclaredType) typeElement.asType();
    env.elements().getAllMembers(typeElement).stream()
        .filter(member -> !member.getModifiers().contains(Modifier.STATIC))
        .filter(emitted)
        .filter(member -> !TsElement.of(member, env).isTsTypeRef())
        .filter(
            member ->
                mentions(declaredProducedType(member), producedType(generic, member), parameter))
        .findFirst()
        .ifPresent(
            member ->
                env.messager()
                    .printMessage(
                        Diagnostic.Kind.WARNING,
                        "Wildcard ["
                            + argument
                            + "] is emitted as ["
                            + bound
                            + "]"
                            + emissionContext
                            + ", but "
                            + typeElement.getSimpleName()
                            + "."
                            + member.getSimpleName()
                            + " produces its type parameter "
                            + parameter
                            + ", so TypeScript is shown "
                            + bound
                            + " where Java only promises a supertype of it."
                            + " Annotate the wildcard with @TsTypeRef to choose the emitted type.",
                        element));
  }

  /** The type a member hands to callers: a method's return type or a field's type, else null. */
  private TypeMirror producedType(DeclaredType containing, Element member) {
    switch (member.getKind()) {
      case METHOD:
        return ((ExecutableType) env.types().asMemberOf(containing, member)).getReturnType();
      case FIELD:
        return env.types().asMemberOf(containing, member);
      default:
        return null;
    }
  }

  /** As {@link #producedType}, but as written on the member, keeping its type-use annotations. */
  private TypeMirror declaredProducedType(Element member) {
    switch (member.getKind()) {
      case METHOD:
        return ((ExecutableElement) member).getReturnType();
      case FIELD:
        return member.asType();
      default:
        return null;
    }
  }

  /**
   * Whether {@code type} names {@code typeVariable} anywhere TypeScript will see it. {@code
   * declared} is the same type as written, walked alongside because substitution drops annotations:
   * a part replaced by {@code @TsTypeRef} there is skipped. Null where the two stop matching, below
   * a substituted type variable.
   */
  private boolean mentions(TypeMirror declared, TypeMirror type, TypeMirror typeVariable) {
    if (!nonNull(type) || (nonNull(declared) && getTypeOrTypeRef(declared) != declared)) {
      return false;
    }
    boolean parallel = nonNull(declared) && declared.getKind().equals(type.getKind());
    switch (type.getKind()) {
      case TYPEVAR:
        return env.types().isSameType(type, typeVariable);
      case DECLARED:
        List<? extends TypeMirror> arguments = ((DeclaredType) type).getTypeArguments();
        List<? extends TypeMirror> declaredArguments =
            parallel ? ((DeclaredType) declared).getTypeArguments() : null;
        for (int i = 0; i < arguments.size(); i++) {
          TypeMirror declaredArgument =
              nonNull(declaredArguments) && declaredArguments.size() == arguments.size()
                  ? declaredArguments.get(i)
                  : null;
          if (mentions(declaredArgument, arguments.get(i), typeVariable)) {
            return true;
          }
        }
        return false;
      case ARRAY:
        return mentions(
            parallel ? ((ArrayType) declared).getComponentType() : null,
            ((ArrayType) type).getComponentType(),
            typeVariable);
      case WILDCARD:
        WildcardType wildcard = (WildcardType) type;
        WildcardType declaredWildcard = parallel ? (WildcardType) declared : null;
        return mentions(
                parallel ? declaredWildcard.getExtendsBound() : null,
                wildcard.getExtendsBound(),
                typeVariable)
            || mentions(
                parallel ? declaredWildcard.getSuperBound() : null,
                wildcard.getSuperBound(),
                typeVariable);
      default:
        return false;
    }
  }

  private TsType typeArgument(TypeMirror typeArgument) {
    Optional<TsType> literalType = TsElement.getLiteralTypeFromMirror(typeArgument, env);
    if (literalType.isPresent()) {
      return literalType.get();
    }
    TypeMirror typeMirror = getTypeOrTypeRef(typeArgument);
    return TsElement.of(typeMirror, env).typeOrNullable(toTsType(typeMirror, true));
  }

  public TypeMirror getTypeOrTypeRef(TypeMirror typeMirror) {
    List<? extends AnnotationMirror> annotations = typeMirror.getAnnotationMirrors();
    return annotations.stream()
        .filter(
            annotationMirror -> isSameType(annotationMirror.getAnnotationType(), TsTypeRef.class))
        .map(annotationMirror -> getValueFromAnnotationMirror(annotationMirror, "value"))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElse(typeMirror);
  }

  public boolean isTsReadOnlyType(TypeMirror typeMirror) {
    List<? extends AnnotationMirror> annotations = typeMirror.getAnnotationMirrors();
    return annotations.stream()
        .anyMatch(
            annotationMirror -> isSameType(annotationMirror.getAnnotationType(), TsReadOnly.class));
  }

  public boolean isSameType(TypeMirror typeMirror, Class<?> targetClass) {
    return env.types()
        .isSameType(
            typeMirror, env.elements().getTypeElement(targetClass.getCanonicalName()).asType());
  }

  public static boolean isSameType(
      TypeMirror typeMirror, Class<?> targetClass, HasProcessorEnv env) {
    return env.types()
        .isSameType(
            typeMirror, env.elements().getTypeElement(targetClass.getCanonicalName()).asType());
  }

  public static boolean isVoidType(TypeMirror typeMirror, HasProcessorEnv env) {
    return typeMirror.getKind() == TypeKind.VOID || isSameType(typeMirror, Void.class, env);
  }

  public Optional<TypeMirror> getValueFromAnnotationMirror(AnnotationMirror am, String paramName) {
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
        am.getElementValues().entrySet()) {
      if (paramName.equals(entry.getKey().getSimpleName().toString())) {
        AnnotationValue annotationValue = entry.getValue();
        return Optional.of((DeclaredType) annotationValue.getValue());
      }
    }
    return Optional.empty();
  }

  public Optional<TsType> fromTsTypeDef(Element element) {
    TsElement tsElement = TsElement.of(element, env);
    if (tsElement.isTsTypeDef()) {
      TsTypeDef tsTypeDef = element.getAnnotation(TsTypeDef.class);
      if (nonNull(tsTypeDef.name())
          && !tsTypeDef.name().trim().isEmpty()
          && !"<auto>".equals(tsTypeDef.name())) {
        return Optional.of(
            TsCustomType.of(
                tsTypeDef.name(), tsElement.getNamespace(), TsType.of(tsTypeDef.tsType())));
      } else {
        return Optional.of(
            TsCustomType.of(
                tsElement.getName() + "Type",
                tsElement.getNamespace(),
                TsType.of(tsTypeDef.tsType())));
      }

    } else if (tsElement.isJsType()) {
      return Optional.of(TsType.of(tsElement.getName(), tsElement.getNamespace()));
    }
    return Optional.empty();
  }
}
