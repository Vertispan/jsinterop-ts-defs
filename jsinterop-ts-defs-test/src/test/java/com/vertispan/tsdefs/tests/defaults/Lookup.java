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
package com.vertispan.tsdefs.tests.defaults;

import com.vertispan.tsdefs.annotations.TsDefault;
import com.vertispan.tsdefs.annotations.TsInterface;
import com.vertispan.tsdefs.tests.wildcards.Item;
import com.vertispan.tsdefs.tests.wildcards.SpecialItem;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Any;

/** Method type parameters with defaults, used when TypeScript has nothing to infer from. */
@TsInterface
public interface Lookup {
  @JsMethod
  <@TsDefault(Any.class) T> T read(String name);

  @JsMethod
  <@TsDefault(SpecialItem.class) T extends Item> T readItem(String name);
}
