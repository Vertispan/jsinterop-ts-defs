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
package com.vertispan.tsdefs.tests.tsdocs.doclet.links.labels;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/** A native type the doclet still emits, because it is a @TsInterface. */
@TsInterface
@JsType(isNative = true, namespace = "probe", name = "Shape")
public interface NativeShape {
  @JsProperty
  double getX();
}
