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
import com.vertispan.tsdefs.tests.wildcards.SpecialItem;
import jsinterop.annotations.JsMethod;

/**
 * Forwards a defaulted parameter to a defaulted super interface. The default must only be emitted
 * on the declaration, never in the extends clause.
 */
@TsInterface
public interface SubDefaultedBox<@TsDefault(SpecialItem.class) T extends SpecialItem>
    extends DefaultedBox<T> {
  @JsMethod
  T peek();
}
