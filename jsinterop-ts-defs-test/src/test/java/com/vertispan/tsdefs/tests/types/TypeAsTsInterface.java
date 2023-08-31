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
package com.vertispan.tsdefs.tests.types;

import com.vertispan.tsdefs.annotations.TsInterface;
import com.vertispan.tsdefs.annotations.TsName;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;

@TsInterface
@TsName(namespace = JsPackage.GLOBAL)
public class TypeAsTsInterface implements FooInterface {

  private int id;

  @JsProperty(name = "firstName")
  public String name;

  public String shouldNotExport() {
    return null;
  }

  @Override
  public String getSomeValue() {
    return null;
  }

  @Override
  public void setSomeValue(String value) {}

  @JsMethod
  String get(long index) {
    return null;
  }

  String get(int index) {
    return null;
  }

  @JsMethod
  public String shouldBeExportedInInheritors() {
    return null;
  }
}
