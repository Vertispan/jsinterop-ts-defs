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

import java.lang.annotation.*;

/**
 * Marks a method or field as being typed in TS as a literal rather than the declared type.
 *
 * <p>Constant fields can omit the value and will use the Java constant value. Other uses, such as
 * method return types, parameters, or type-use annotations, should supply an explicit literal
 * value.
 *
 * <p>Supported types are {@code String}, {@code boolean}/{@code Boolean}, {@code double}/{@code
 * Double}, and {@code int}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Documented
public @interface TsLiteral {
  /**
   * The literal value to emit in the TypeScript definition. If empty, the Java constant value of
   * the annotated field will be used instead.
   */
  String value() default "";
}
