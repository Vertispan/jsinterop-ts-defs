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
package com.vertispan.tsdefs.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A type annotated with this annotation will represent a Typescript Union type equivalent, and
 * should define {@link TsUnionMember}s that will be used to generate the correct union type, When a
 * type element is annotated with this annotation is used as a type for class member or parameter
 * the tool will emit a union type of all {@link TsUnionMember}s included in the type element
 * instead of the type itself.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface TsUnion {
  /**
   * Controls whether the union is emitted inline at each usage site. By default, the union is
   * anonymous and the generated TypeScript type is the union of all {@link TsUnionMember} types.
   * Set this to {@code false} to emit a named TypeScript type definition that can be referenced by
   * name. A name or namespace must not be specified for an anonymous union.
   */
  boolean anonymous() default true;

  /**
   * The TypeScript name to use when {@link #anonymous()} is {@code false}. The default derives the
   * name from the Java type, ignoring {@code @JsType(name = ...)}. {@code @TsName} remains
   * supported as a backwards-compatible override.
   */
  String name() default "<auto>";

  /**
   * The TypeScript namespace to use when {@link #anonymous()} is {@code false}. The default uses
   * the Java package namespace, ignoring {@code @JsType(namespace = ...)}. Use {@code
   * JsPackage.GLOBAL} for the global namespace.
   */
  String namespace() default "<auto>";
}
