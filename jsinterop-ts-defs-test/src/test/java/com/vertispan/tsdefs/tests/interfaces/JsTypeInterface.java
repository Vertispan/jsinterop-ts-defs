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
package com.vertispan.tsdefs.tests.interfaces;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public interface JsTypeInterface {

  @JsIgnore String property = "INTERFACE STATIC PROPERTY";

  String methodOne();

  @JsMethod(name = "methodTwo")
  String methodRenamed();

  @JsProperty
  String getPropertyA();

  @JsProperty
  void setPropertyA(String propertyA);

  // What should we do with interface method with a different namespace?
  @JsMethod(namespace = "com.vertispan.differentNameSpace.OtherType")
  String methodFromJsTypeInterface();
}
