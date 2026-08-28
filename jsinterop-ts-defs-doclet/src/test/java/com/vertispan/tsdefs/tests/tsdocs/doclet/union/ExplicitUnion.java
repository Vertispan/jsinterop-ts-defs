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
package com.vertispan.tsdefs.tests.tsdocs.doclet.union;

import com.vertispan.tsdefs.annotations.TsLiteral;
import com.vertispan.tsdefs.annotations.TsUnion;
import com.vertispan.tsdefs.annotations.TsUnionMember;
import elemental2.core.JsArray;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/** A generic named union containing a number or an array of its type parameter. */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
@TsUnion(anonymous = false)
public interface ExplicitUnion<T> {
  @JsOverlay
  @TsUnionMember
  default Double asNumber() {
    return null;
  }

  @JsOverlay
  @TsUnionMember
  default JsArray<T> asArray() {
    return null;
  }

  @TsUnionMember @TsLiteral @JsOverlay String FOO = "foo";
}
