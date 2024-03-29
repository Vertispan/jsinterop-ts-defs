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
package com.vertispan.tsdefs.tests.constructors;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
public class JsTypeWithPrivateAndIgnoredConstructors {

  public String property1;
  public String property2;

  private JsTypeWithPrivateAndIgnoredConstructors(String property) {
    this.property1 = property;
    this.property2 = property;
  }

  @JsIgnore
  public JsTypeWithPrivateAndIgnoredConstructors(String property1, String property2) {
    this.property1 = property1;
    this.property2 = property2;
  }
}
