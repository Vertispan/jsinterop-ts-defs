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
package com.vertispan.tsdefs.tests.methods;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;

public class NonJsTypeWithMethods {

  @JsConstructor
  public NonJsTypeWithMethods() {}

  @JsMethod
  public void takesNothingReturnVoidExported() {}

  public void takesNothingReturnVoid() {}

  @JsMethod(name = "takesNothingReturnVoidRenamed")
  public void takesNothingReturnVoidWithDifferentName() {}

  public String takesDoubleReturnString(double value) {
    return "";
  }

  public String takesMultipleParamsReturnString(
      String stringParam, double doubleParam, boolean booleanParam) {
    return "";
  }

  @JsMethod
  protected String protectedMethod() {
    return "";
  }

  protected String protectedButNotExportedMethod() {
    return "";
  }

  @JsMethod
  private String privateMethodButExported(double value) {
    return "";
  }

  private String privateMethodButNotExported(double value) {
    return "";
  }

  @JsMethod(name = "doSomethingElse", namespace = "com.vertispan.differentNameSpace.OtherType")
  public String methodWithDifferentNamespace() {
    return "";
  }
}
