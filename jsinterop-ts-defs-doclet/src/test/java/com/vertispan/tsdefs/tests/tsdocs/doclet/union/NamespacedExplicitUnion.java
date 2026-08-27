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

import com.vertispan.tsdefs.annotations.TsUnion;
import com.vertispan.tsdefs.annotations.TsUnionMember;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/** A named union emitted inside a TypeScript namespace. */
@JsType(isNative = true, namespace = "com.vertispan.tsdefs.tests.tsdocs.doclet.union")
@TsUnion(anonymous = false)
public interface NamespacedExplicitUnion {
  @JsOverlay
  @TsUnionMember
  default String asString() {
    return null;
  }

  @JsOverlay
  @TsUnionMember
  default Boolean asBoolean() {
    return null;
  }
}
