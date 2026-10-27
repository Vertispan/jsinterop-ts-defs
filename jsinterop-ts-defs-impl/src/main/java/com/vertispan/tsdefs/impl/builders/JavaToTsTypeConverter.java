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
import javax.lang.model.type.WildcardType;
import javax.tools.Diagnostic;
import jsinterop.base.JsPropertyMap;

public class JavaToTsTypeConverter {
  private final HasProcessorEnv env;
  private final Element element;
  private final String emissionContext;

  public JavaToTsTypeConverter(Element element, HasProcessorEnv env) {
    this(element, "", env);
  }

  /**
   * @param inheritedBy the subtype whose TypeScript declaration {@code element} is being emitted
   *     into, when that is not the type that declares it; inherited members are emitted once per
   *     subtype
   */
  public JavaToTsTypeConverter(Element element, TypeElement inheritedBy, HasProcessorEnv env) {
    this(element, inheritedSuffix(element, inheritedBy), env);
  }

  /** Converts the type arguments in {@code subtype}'s extends and implements clauses. */
  public static JavaToTsTypeConverter forSupertypesOf(TypeElement subtype, HasProcessorEnv env) {
    return new JavaToTsTypeConverter(
        subtype, " in the supertypes of " + subtype.getSimpleName(), env);
  }

  /**
   * @param emissionContext appended to diagnostics about {@code element}'s types when they are
   *     emitted somewhere other than its own declaration, or empty
   */
  private JavaToTsTypeConverter(Element element, String emissionContext, HasProcessorEnv env) {
    this.env = env;
    this.element = element;
    this.emissionContext = emissionContext;
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

      if (ProcessorType.of(type, env).is2dArray()) {
        TypeMirror arrayComponentType = ProcessorType.of(type, env).deepArrayComponentType();
        if (ProcessorType.of(arrayComponentType, env).isUnionType()) {
          return TsElement.of(arrayComponentType, env)
              .typeOrNullable(
                  ParameterizedTsType.of(
                      "Array",
                      "",
                      ParameterizedTsType.of("Array", "", toTsType(arrayComponentType))));
        } else {
          return TsElement.of(arrayComponentType, env)
              .typeOrNullable(Array2dTsType.of(toTsType(arrayComponentType)));
        }
      }

      if (ProcessorType.of(type, env).isArray()) {
        TypeMirror arrayComponentType = ProcessorType.of(type, env).arrayComponentType();
        if (ProcessorType.of(arrayComponentType, env).isUnionType()) {
          return TsElement.of(arrayComponentType, env)
              .typeOrNullable(ParameterizedTsType.of("Array", "", toTsType(arrayComponentType)));
        } else {
          ArrayTsType arrayType = ArrayTsType.of(toTsType(arrayComponentType));
          arrayType.setTsReadOnly(
              TsElement.of(element, env).isTsReadOnly() || isTsReadOnlyType(element.asType()));
          return TsElement.of(arrayComponentType, env).typeOrNullable(arrayType);
        }
      }

      if (ProcessorType.of(type, env).isAssignableFrom(JsPropertyMap.class)) {
        DeclaredType declaredType = (DeclaredType) type;
        // The index signature reads and writes, so every member counts as emitted.
        warnIfSuperWildcardIsProduced(declaredType, 0, member -> true);
        return TsTemplatedInlinedType.of(
            "{ [key: string]: $T; }", asTypeWithArgument(declaredType).get(0));
      }

      if (ProcessorType.of(type, env).isJsFunction()) {
        return inlineJsFunctionType(type);
      }

      if (ProcessorType.of(type, env).isUnionType()) {
        if (TsElement.of(type, env).isExplicitUnionType()) {
          return getDeclaredType((DeclaredType) type, TsElement.of(type, env));
        }
        return unionType(type);
      }

      return getReferenceType(type);
    }
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
                return e.typeOrNullable(
                    toTsType(
                        ((ExecutableType) env.types().asMemberOf(declaredType, e.element()))
                            .getReturnType()));
              }
              return e.getType();
            })
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private TsInlinedFunctionType inlineJsFunctionType(TypeMirror type) {
    Optional<TsElement> first =
        TsElement.of(type, env).element().getEnclosedElements().stream()
            .filter(element -> ElementKind.METHOD.equals(element.getKind()))
            .map(element -> TsElement.of(element, env))
            .filter(tsElement -> !tsElement.isOverlay() && !tsElement.isIgnored())
            .findFirst();

    TsInlinedFunctionType tsType = new TsInlinedFunctionType();
    ExecutableElement executableElement = (ExecutableElement) first.get().element();
    List<? extends TypeMirror> declaredArguments = ((DeclaredType) type).getTypeArguments();
    List<? extends TypeMirror> argumentVars =
        ((DeclaredType) executableElement.getEnclosingElement().asType()).getTypeArguments();
    Map<String, TsType> typeMapping = new HashMap<>();
    argumentVars.forEach(
        typeMirror -> {
          int index = argumentVars.indexOf(typeMirror);
          // Only the function method is emitted, as the inlined signature.
          warnIfSuperWildcardIsProduced(
              (DeclaredType) type, index, member -> member.equals(executableElement));
          typeMapping.put(typeMirror.toString(), typeArgument(declaredArguments.get(index)));
        });
    executableElement
        .getParameters()
        .forEach(
            param ->
                new ParameterVisitor<TsInlinedFunctionType>(param, typeMapping, env).visit(tsType));

    TypeMirror returnType = executableElement.getReturnType();
    if (TypeKind.TYPEVAR.equals(returnType.getKind())
        && typeMapping.containsKey(returnType.toString())) {
      tsType.setReturnType(typeMapping.get(returnType.toString()));
    } else {
      tsType.setReturnType(toTsType(executableElement.getReturnType()));
    }
    return tsType;
  }

  private TsType getReferenceType(TypeMirror type) {
    if (TypeKind.TYPEVAR.equals(type.getKind())) {
      return TsType.of(type.toString(), "");
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
   * arguments is not tracked.
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
    if (!nonNull(bound) || getTypeOrTypeRef(bound) != bound || index >= parameters.size()) {
      return;
    }
    TypeMirror parameter = parameters.get(index).asType();
    DeclaredType generic = (DeclaredType) typeElement.asType();
    env.elements().getAllMembers(typeElement).stream()
        .filter(member -> !member.getModifiers().contains(Modifier.STATIC))
        .filter(emitted)
        .filter(member -> mentions(producedType(generic, member), parameter))
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
                            + ", so TypeScript callers may receive a supertype of "
                            + bound
                            + ". Annotate the wildcard with @TsTypeRef to choose the emitted type.",
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

  private boolean mentions(TypeMirror type, TypeMirror typeVariable) {
    if (!nonNull(type)) {
      return false;
    }
    switch (type.getKind()) {
      case TYPEVAR:
        return env.types().isSameType(type, typeVariable);
      case DECLARED:
        return ((DeclaredType) type)
            .getTypeArguments().stream().anyMatch(argument -> mentions(argument, typeVariable));
      case ARRAY:
        return mentions(((ArrayType) type).getComponentType(), typeVariable);
      case WILDCARD:
        WildcardType wildcard = (WildcardType) type;
        return mentions(wildcard.getExtendsBound(), typeVariable)
            || mentions(wildcard.getSuperBound(), typeVariable);
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
