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

import com.vertispan.tsdefs.annotations.TsTypeRef;
import elemental2.core.JsArray;
import elemental2.core.JsString;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType
public class EnumClient {

  public void useEnum(@TsTypeRef(EnumSimulation.class) String param) {}

  public void useString(String param) {}

  public void useJsTypeAsTypeReference(@TsTypeRef(UsedAsTypeRef.class) Object param) {}

  public JsPropertyMap<JsArray<@TsTypeRef(EnumSimulation.class) JsString>> values;
}
