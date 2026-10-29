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
import javax.validation.constraints.NotNull;

/**
 * Use this annotation on a type parameter to emit a TypeScript default type argument, e.g. {@code
 * <T extends Item = Item>}. The default must satisfy the parameter's bound, and every type
 * parameter after a defaulted one must also have a default.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_PARAMETER)
@Documented
public @interface TsDefault {
  @NotNull
  Class<?> value();
}
