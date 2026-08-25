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
package com.vertispan.tsdefs.tests.literals;

import com.vertispan.tsdefs.annotations.TsLiteral;
import com.vertispan.tsdefs.annotations.TsUnion;
import com.vertispan.tsdefs.annotations.TsUnionMember;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(name = "?", namespace = JsPackage.GLOBAL, isNative = true)
@TsUnion
public interface UnionWithDiscriminator {
  @JsType
  public static class Foo {
    @TsLiteral public final String type = "foo";
  }

  @JsType
  public static class Bar {
    @TsLiteral public final String type = "bar";
  }

  @JsOverlay
  @TsUnionMember
  default Foo asFoo() {
    return Js.cast(this);
  }

  @JsOverlay
  @TsUnionMember
  default Bar asBar() {
    return Js.cast(this);
  }
}
