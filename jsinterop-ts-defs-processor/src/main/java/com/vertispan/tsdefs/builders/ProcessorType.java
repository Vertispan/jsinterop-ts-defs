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

import com.vertispan.tsdefs.HasProcessorEnv;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class ProcessorType {
  private final TypeMirror type;
  private HasProcessorEnv env;

  public ProcessorType(TypeMirror type, HasProcessorEnv env) {
    this.type = type;
    this.env = env;
  }

  public static ProcessorType of(TypeMirror typeMirror, HasProcessorEnv env) {
    return new ProcessorType(typeMirror, env);
  }

  /**
   * Checks if the type mirror is a primitive.
   *
   * @return {@code true} if the type is a primitive, {@code false} otherwise
   */
  public boolean isPrimitive() {
    return type.getKind().isPrimitive();
  }

  /**
   * Checks if the type mirror is a primitive array.
   *
   * @return {@code true} if the type is a primitive array, {@code false} otherwise
   */
  public boolean isPrimitiveArray() {
    return (isArray() && ProcessorType.of(arrayComponentType(), env).isPrimitive())
        || isPrimitive2dArray();
  }

  /**
   * Checks if the type mirror is a primitive 2D array.
   *
   * @return {@code true} if the type is a primitive 2D array, {@code false} otherwise
   */
  private boolean isPrimitive2dArray() {
    return is2dArray() && ProcessorType.of(arrayComponentType(), env).isPrimitiveArray();
  }

  /**
   * Checks if the type mirror is an array.
   *
   * @return {@code true} if the type is an array, {@code false} otherwise
   */
  public boolean isArray() {
    return TypeKind.ARRAY.compareTo(type.getKind()) == 0;
  }

  /**
   * Checks if the type mirror is a 2D array.
   *
   * @return {@code true} if the type is a 2D array, {@code false} otherwise
   */
  public boolean is2dArray() {
    return isArray() && ProcessorType.of(arrayComponentType(), env).isArray();
  }

  /**
   * Returns the component type of an array.
   *
   * @return the component type of an array
   */
  public TypeMirror arrayComponentType() {
    return ((ArrayType) type).getComponentType();
  }

  /**
   * Returns the component type of array <em>recursively</em>.
   *
   * @return the most inner component type of array
   */
  public TypeMirror deepArrayComponentType() {
    TypeMirror componentType = ((ArrayType) type).getComponentType();
    return ProcessorType.of(componentType, env).isArray()
        ? ProcessorType.of(componentType, env).arrayComponentType()
        : componentType;
  }

  /**
   * Checks if the type is assignable of a {@link Collection}
   *
   * @return {@code true} if the type is a {@link Collection} or assignable from, {@code false}
   *     otherwise.
   */
  public boolean isCollection() {
    return !isPrimitive() && isAssignableFrom(Collection.class);
  }

  /**
   * Checks if the type is assignable of a {@link Iterable}
   *
   * @param typeMirror the type
   * @return {@code true} if the type is a {@link Iterable} or assignable from, {@code false}
   *     otherwise.
   */
  public boolean isIterable(TypeMirror typeMirror) {
    return !isPrimitive() && isAssignableFrom(Iterable.class);
  }

  /**
   * Checks if the type mirror is assignable of the target class (i.e. if it's a derivative type of
   * it)
   *
   * @param targetClass the target class
   * @return true if the type mirror is assignable from the target class, false otherwise
   */
  public boolean isAssignableFrom(Class<?> targetClass) {
    return env.types()
        .isAssignable(
            type,
            env.types()
                .getDeclaredType(env.elements().getTypeElement(targetClass.getCanonicalName())));
  }

  public boolean isSet() {
    return isAssignableFrom(Set.class);
  }

  /**
   * Checks if the type is assignable of a {@link Map}
   *
   * @return {@code true} if the type is a {@link Map} or assignable from, {@code false} otherwise.
   */
  public boolean isMap() {
    return !isPrimitive() && isAssignableFrom(Map.class);
  }

  public boolean isJsFunction() {
    Element element = env.types().asElement(type);
    return nonNull(element) && TsElement.of(element, env).isJsFunction();
  }
}
