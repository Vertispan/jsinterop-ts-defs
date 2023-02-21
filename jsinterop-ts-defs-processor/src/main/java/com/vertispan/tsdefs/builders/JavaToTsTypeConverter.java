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
package com.vertispan.tsdefs.builders;

import static java.util.Objects.nonNull;

import com.vertispan.tsdefs.Formatting;
import com.vertispan.tsdefs.HasProcessorEnv;
import com.vertispan.tsdefs.annotations.TsTypeDef;
import com.vertispan.tsdefs.annotations.TsTypeRef;
import com.vertispan.tsdefs.model.*;
import com.vertispan.tsdefs.visitors.ParameterVisitor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import jsinterop.base.JsPropertyMap;

public class JavaToTsTypeConverter {
  private final HasProcessorEnv env;
  private final Element element;

  public JavaToTsTypeConverter(Element element, HasProcessorEnv env) {
    this.env = env;
    this.element = element;
  }

  public TsType toTsType(TypeMirror type) {
    return toTsType(type, false);
  }

  public TsType toTsType(TypeMirror type, boolean checkTypeDef) {

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
        return Array2dTsType.of(toTsType(ProcessorType.of(type, env).arrayComponentType()));
      }

      if (ProcessorType.of(type, env).isArray()) {
        return ArrayTsType.of(toTsType(ProcessorType.of(type, env).arrayComponentType()));
      }

      if (ProcessorType.of(type, env).isAssignableFrom(JsPropertyMap.class)) {
        DeclaredType declaredType = (DeclaredType) type;
        return TsTemplatedInlinedType.of(
            "{ [key: string]: $T; }", asTypeWithArgument(declaredType).get(0));
      }

      if (ProcessorType.of(type, env).isJsFunction()) {
        return inlineJsFunctionType(type);
      }

      return getReferenceType(type);
    }
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
        typeMirror ->
            typeMapping.put(
                typeMirror.toString(),
                toTsType(declaredArguments.get(argumentVars.indexOf(typeMirror)))));
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
      if (nonNull(declaredType.asElement().getAnnotation(TsTypeRef.class))) {
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
    String name = typeElement.getName();
    String nameSpace = typeElement.isJsFunction() ? "" : typeElement.getNamespace();

    if (!declaredType.getTypeArguments().isEmpty()) {
      return ParameterizedTsType.of(name, nameSpace, asTypeWithArgument(declaredType));
    }
    return TsType.of(name, nameSpace);
  }

  private TsType getTsTypeReference(DeclaredType declaredType) {
    Optional<TypeMirror> typeRef =
        TsElement.of(declaredType.asElement(), env)
            .getClassValueFromAnnotation(TsTypeRef.class, "value");
    return toTsType(typeRef.get());
  }

  private List<TsType> asTypeWithArgument(DeclaredType type) {
    List<? extends TypeMirror> typeArguments = type.getTypeArguments();

    return typeArguments.stream()
        .map(this::getTypeOrTypeRef)
        .map(typeMirror -> toTsType(typeMirror, true))
        .collect(Collectors.toList());
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
