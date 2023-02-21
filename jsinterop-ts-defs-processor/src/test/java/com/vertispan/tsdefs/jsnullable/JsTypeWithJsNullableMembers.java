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
package com.vertispan.tsdefs.jsnullable;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsNullable;
import jsinterop.annotations.JsType;

@JsType
public class JsTypeWithJsNullableMembers {

  @JsIgnore public String ignoredProperty;
  public String notNullable;

  public @JsNullable String nullableProperty;

  public String notNullableMethod() {
    return null;
  }

  public @JsNullable String nullableMethod() {
    return null;
  }

  public String methodWithNullableParameter(@JsNullable String nullable, String notNullable) {
    return "";
  }
}
