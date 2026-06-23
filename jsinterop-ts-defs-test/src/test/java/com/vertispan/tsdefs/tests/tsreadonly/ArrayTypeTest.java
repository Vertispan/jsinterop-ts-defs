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
package com.vertispan.tsdefs.tests.tsreadonly;

import com.vertispan.tsdefs.annotations.TsReadOnly;
import elemental2.core.JsArray;
import elemental2.core.JsMap;
import elemental2.core.ReadonlyArray;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType
public class ArrayTypeTest {

  public @TsReadOnly String readonlyProperty;
  public @TsReadOnly JsPropertyMap<String> mutableProperty;
  public JsPropertyMap<@TsReadOnly String> mutableTypeArg;
  public JsArray<String> jsArrayProperty;
  public JsArray<@TsReadOnly String> jsArrayOfTsReadonly;
  public JsMap<@TsReadOnly String, String> jsMapOfTsReadonlyStringAndString;
  public @TsReadOnly JsMap<String, String> tsReadonlyJsMapOfStringAndString;
  public @TsReadOnly String[] tsReadonlyReadonlyArrayProperty;
  public String @TsReadOnly [] tsReadonlyReadonlyArrayPropertyWithAnnotation;
  public @TsReadOnly String[][] tsReadonly2dArrayProperty;
  public String @TsReadOnly [][] tsReadonlyType2dArrayProperty;
  public final String[] finalReadonlyArrayProperty;

  public ArrayTypeTest() {
    this.finalReadonlyArrayProperty = new String[0];
  }

  public ReadonlyArray<String> doSomethingA() {
    return null;
  }

  public String takesTsReadonlyArg(@TsReadOnly String arg) {
    return null;
  }

  public String takesTsReadonlyTypeArg(@TsReadOnly JsMap<@TsReadOnly String, String> arg) {
    return null;
  }

  public @TsReadOnly String doSomethingAndReturnReadonlyString() {
    return "";
  }
}
