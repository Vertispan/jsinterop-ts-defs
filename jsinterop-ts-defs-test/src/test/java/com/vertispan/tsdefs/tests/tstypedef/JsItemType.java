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
package com.vertispan.tsdefs.tests.tstypedef;

import com.vertispan.tsdefs.annotations.TsTypeDef;
import jsinterop.annotations.JsType;

@JsType(namespace = "com.vertispan.storage", name = "ItemType")
@TsTypeDef(tsType = "string")
public class JsItemType {
  public static final String DIRECTORY = "directory";

  public static final String FILE = "file";
}
