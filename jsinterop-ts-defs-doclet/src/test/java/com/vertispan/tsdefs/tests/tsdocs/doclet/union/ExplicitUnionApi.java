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
package com.vertispan.tsdefs.tests.tsdocs.doclet.union;

import jsinterop.annotations.JsNullable;
import jsinterop.annotations.JsType;

/** API methods that consume and return named union types. */
@JsType
public interface ExplicitUnionApi {
  /** Returns a nullable explicit union. */
  @JsNullable
  ExplicitParamUnion getExplicitUnion();

  /** Uses a generic explicit union containing another named union. */
  ExplicitUnion<ExplicitParamUnion> useExplicitUnion(
      ExplicitUnion<ExplicitParamUnion> value);
}
