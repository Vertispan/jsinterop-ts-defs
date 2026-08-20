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
import jsinterop.annotations.JsType;

@JsType
public class ClassWithLiteralAPIs {
  public static void acceptsUnionWithLiterals(LiteralUnion param) {}

  public static void acceptsDiscriminatedUnion(UnionWithDiscriminator param) {}

  public static @TsLiteral("ready") String returnsLiteral() {
    return "ready";
  }

  public static void acceptsLiteral(@TsLiteral("literal") String param) {}

  public static @TsLiteral("42") double returnsNumericLiteral() {
    return 42;
  }

  public static void acceptsNumericLiteral(@TsLiteral("42") int param) {}

  public static @TsLiteral("true") boolean returnsBooleanLiteral() {
    return true;
  }

  public static void acceptsBooleanLiteral(@TsLiteral("false") boolean param) {}
}
