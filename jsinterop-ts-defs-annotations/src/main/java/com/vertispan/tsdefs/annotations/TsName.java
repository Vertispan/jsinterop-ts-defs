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
package com.vertispan.tsdefs.annotations;

import java.lang.annotation.*;
import javax.validation.constraints.NotNull;

/**
 * Use this annotation on a type to override its name/namespace in the exported file, the values
 * from this annotation will override the values provided by the JsInterop annotations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface TsName {
  @NotNull
  String name() default "<auto>";

  @NotNull
  String namespace() default "<auto>";
}
