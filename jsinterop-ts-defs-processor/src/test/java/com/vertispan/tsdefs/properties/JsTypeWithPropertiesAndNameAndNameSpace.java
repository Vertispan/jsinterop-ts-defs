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
package com.vertispan.tsdefs.properties;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(
    name = "DifferentJsTypeWithPropertiesAndNameAndNameSpace",
    namespace = "com.vertispan.differentNameSpace")
public class JsTypeWithPropertiesAndNameAndNameSpace {

  public String property;

  @JsProperty(name = "theOtherProperty", namespace = "com.vertispan.differentNameSpace.OtherType")
  public String otherProperty;

  @JsProperty(
      name = "theThirdProperty",
      namespace =
          "com.vertispan.differentNameSpace.DifferentJsTypeWithPropertiesAndNameAndNameSpace")
  public String thirdProperty;

  @JsProperty(namespace = "com.vertispan.differentNameSpace")
  public String fourthProperty;
}
