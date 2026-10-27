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
import com.vertispan.tsdefs.impl.builders.HasTypeArguments;
import com.vertispan.tsdefs.impl.builders.JavaToTsTypeConverter;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsType;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Adds a type's type parameters to a builder: with bounds for the type's own declaration ({@link
 * #ofDeclaration}), without bounds where the type is used as a supertype or interface.
 */
public class TypeArgumentsVisitor<T> extends TsElement {
  private final TypeMirror type;
  private final boolean declaration;

  /**
   * Visits the type arguments of a type in a usage position, such as a superclass or implemented
   * interface. Bounds are never emitted here, only on the declaring type's own type parameters.
   */
  public TypeArgumentsVisitor(TypeMirror typeMirror, HasProcessorEnv env) {
    this(typeMirror, env, false);
  }

  private TypeArgumentsVisitor(TypeMirror typeMirror, HasProcessorEnv env, boolean declaration) {
    super(typeMirror, env);
    this.type = typeMirror;
    this.declaration = declaration;
  }

  /**
   * Visits the type parameters declared by a type, emitting each one along with its bounds, e.g.
   * {@code <T extends Item>}.
   */
  public static <T> TypeArgumentsVisitor<T> ofDeclaration(
      TypeMirror typeMirror, HasProcessorEnv env) {
    return new TypeArgumentsVisitor<>(typeMirror, env, true);
  }

  public void visit(HasTypeArguments<T> builder) {
    DeclaredType classDeclaredType = (DeclaredType) type;
    if (classDeclaredType.getTypeArguments().isEmpty()) {
      return;
    }
    if (declaration) {
      declaredTypeParameters(classDeclaredType, env).forEach(builder::addTypeArgument);
    } else {
      new JavaToTsTypeConverter(element, env)
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
      List<? extends TypeMirror> bounds = ((TypeParameterElement) typeElement).getBounds();

      bounds.stream()
          .filter(boundType -> !JavaToTsTypeConverter.isSameType(boundType, Object.class, env))
          .map(boundType -> TsElement.of(boundType, env).getType())
          .forEach(type::addBounds);
    }
    return type;
  }
}
