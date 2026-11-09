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

import com.vertispan.tsdefs.annotations.TsDefault;
import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.HasTypeArguments;
import com.vertispan.tsdefs.impl.builders.JavaToTsTypeConverter;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Adds a type's type parameters to a builder: with bounds for the type's own declaration ({@link
 * #ofDeclaration}), without bounds where the type is used as a supertype or interface.
 */
public class TypeArgumentsVisitor<T> extends TsElement {
  private final TypeMirror type;
  private final TypeElement writer;
  private final TypeElement emittedIn;

  /**
   * Visits the type arguments of a type in a usage position, such as a superclass or implemented
   * interface written by {@code writer}. Bounds are never emitted here, only on the declaring
   * type's own type parameters.
   */
  public TypeArgumentsVisitor(TypeMirror typeMirror, TypeElement writer, HasProcessorEnv env) {
    this(typeMirror, writer, writer, env);
  }

  /**
   * As above, for a clause of a skipped {@code writer} emitted in its subtype {@code emittedIn},
   * which passes the arguments.
   */
  public TypeArgumentsVisitor(
      TypeMirror typeMirror, TypeElement writer, TypeElement emittedIn, HasProcessorEnv env) {
    super(typeMirror, env);
    this.type = typeMirror;
    this.writer = writer;
    this.emittedIn = emittedIn;
  }

  /**
   * Visits the type parameters declared by a type, emitting each one along with its bounds, e.g.
   * {@code <T extends Item>}.
   */
  public static <T> TypeArgumentsVisitor<T> ofDeclaration(
      TypeMirror typeMirror, HasProcessorEnv env) {
    return new TypeArgumentsVisitor<>(typeMirror, null, null, env);
  }

  public void visit(HasTypeArguments<T> builder) {
    DeclaredType classDeclaredType = (DeclaredType) type;
    if (writer != null && JavaToTsTypeConverter.isRaw(classDeclaredType)) {
      String message = JavaToTsTypeConverter.rawTypeMessage(classDeclaredType, writer, true, env);
      if (env.firstReport(writer, message)) {
        env.messager().printMessage(Diagnostic.Kind.ERROR, message, writer);
      }
      return;
    }
    if (classDeclaredType.getTypeArguments().isEmpty()) {
      return;
    }
    if (writer == null) {
      declaredTypeParameters(classDeclaredType, env).forEach(builder::addTypeArgument);
    } else {
      JavaToTsTypeConverter.forSupertypesOf(writer, emittedIn, env)
          .asTypeWithArgument(classDeclaredType)
          .forEach(builder::addTypeArgument);
    }
  }

  /** The type parameters a type declares, each with its bounds, e.g. {@code <T extends Item>}. */
  public static List<TsType> declaredTypeParameters(DeclaredType type, HasProcessorEnv env) {
    return type.getTypeArguments().stream()
        .map(typeMirror -> declaredTypeParameter(typeMirror, env))
        .collect(Collectors.toList());
  }

  private static TsType declaredTypeParameter(TypeMirror typeMirror, HasProcessorEnv env) {
    TsType type = TsElement.of(typeMirror, env).getType();
    Element typeElement = env.types().asElement(typeMirror);
    if (typeElement.getKind().equals(ElementKind.TYPE_PARAMETER)) {
      addBoundsAndDefault(
          type, (TypeParameterElement) typeElement, null, Collections.emptyMap(), env);
    }
    return type;
  }

  /**
   * Adds a type parameter's bounds and {@code @TsDefault} to its declaration, e.g. {@code T extends
   * Item = Item}. Reports an error for a default TypeScript would reject.
   *
   * @param inheritedBy the subtype a method with this type parameter is inherited by, or null
   * @param renamed the method's type parameters emitted under another name
   */
  static void addBoundsAndDefault(
      TsType type,
      TypeParameterElement parameter,
      TypeElement inheritedBy,
      Map<Element, TsType> renamed,
      HasProcessorEnv env) {
    // Convert the bound mirror itself, so Box<Item> keeps its argument instead of emitting the
    // bound type's own declared parameter.
    JavaToTsTypeConverter converter =
        new JavaToTsTypeConverter(parameter, inheritedBy, renamed, env);
    parameter.getBounds().stream()
        .filter(boundType -> !JavaToTsTypeConverter.isSameType(boundType, Object.class, env))
        .map(converter::toTsType)
        .forEach(type::addBounds);

    Optional<TypeMirror> defaultType =
        TsElement.of(parameter, env).getClassValueFromAnnotation(TsDefault.class, "value");
    if (defaultType.isPresent()) {
      // TODO(#142) A class literal can't carry type arguments, so a generic default isn't
      //  supported yet: https://github.com/Vertispan/jsinterop-ts-defs/issues/142
      if (defaultType.get() instanceof DeclaredType
          && JavaToTsTypeConverter.isRaw((DeclaredType) defaultType.get())) {
        env.messager()
            .printMessage(
                Diagnostic.Kind.ERROR,
                "@TsDefault("
                    + defaultType.get()
                    + ") on type parameter "
                    + parameter.getSimpleName()
                    + " of "
                    + ownerName(parameter)
                    + " names a generic type, which a default can't give type arguments to.",
                parameter);
        return;
      }
      TsType tsDefault = converter.toTsType(defaultType.get(), true);
      type.setDefaultType(tsDefault);
      // TypeScript's any satisfies every bound. Bounds are erased, since a class literal is raw.
      if (!"any".equals(tsDefault.emit(""))) {
        parameter.getBounds().stream()
            .filter(
                bound -> !env.types().isAssignable(defaultType.get(), env.types().erasure(bound)))
            .findFirst()
            .ifPresent(
                bound ->
                    env.messager()
                        .printMessage(
                            Diagnostic.Kind.ERROR,
                            "@TsDefault("
                                + defaultType.get()
                                + ") on type parameter "
                                + parameter.getSimpleName()
                                + " of "
                                + ownerName(parameter)
                                + " does not satisfy its bound "
                                + bound
                                + ".",
                            parameter));
      }
    } else if (hasDefaultedPredecessor(parameter, env)) {
      env.messager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              "Type parameter "
                  + parameter.getSimpleName()
                  + " of "
                  + ownerName(parameter)
                  + " has no @TsDefault, but follows one that does. TypeScript only allows"
                  + " defaults on trailing type parameters.",
              parameter);
    }
  }

  private static boolean hasDefaultedPredecessor(
      TypeParameterElement parameter, HasProcessorEnv env) {
    for (TypeParameterElement sibling :
        ((Parameterizable) parameter.getGenericElement()).getTypeParameters()) {
      if (sibling.equals(parameter)) {
        return false;
      }
      if (TsElement.of(sibling, env)
          .getClassValueFromAnnotation(TsDefault.class, "value")
          .isPresent()) {
        return true;
      }
    }
    return false;
  }

  /** The declaring type, or {@code Type.method} for a method's type parameter. */
  private static String ownerName(TypeParameterElement parameter) {
    Element generic = parameter.getGenericElement();
    if (generic instanceof TypeElement) {
      return ((TypeElement) generic).getQualifiedName().toString();
    }
    return ((TypeElement) generic.getEnclosingElement()).getQualifiedName()
        + "."
        + generic.getSimpleName();
  }
}
