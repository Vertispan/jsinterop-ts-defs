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
package com.vertispan.tsdefs.tstypedef;

import com.vertispan.tsdefs.annotations.TsTypeDef;
import elemental2.core.JsObject;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType
@TsTypeDef(tsType = "string")
public class EnumSimulation {
  public static final String VALUE_1 = "value1";
  public static final String VALUE_2 = "value2";
  public static final String VALUE_3 = "value3";

  private static final String[] VALUES = JsObject.freeze(new String[] {VALUE_1, VALUE_2, VALUE_3});

  @JsMethod
  public String[] values() {
    return VALUES;
  }
}
