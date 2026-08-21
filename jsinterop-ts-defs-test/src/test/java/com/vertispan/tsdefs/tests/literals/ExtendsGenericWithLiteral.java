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
package com.vertispan.tsdefs.tests.literals;

import com.vertispan.tsdefs.annotations.TsLiteral;
import com.vertispan.tsdefs.tests.generics.GenericJsType;
import jsinterop.annotations.JsType;

/**
 * Tests that @TsLiteral as a TYPE_USE annotation on a generic type argument produces a literal type
 * in the generated TS output, e.g. {@code class ExtendsGenericWithLiteral extends
 * GenericJsType<"hello">}.
 */
@JsType
public class ExtendsGenericWithLiteral extends GenericJsType<@TsLiteral("hello") String> {
  public String name;
}
