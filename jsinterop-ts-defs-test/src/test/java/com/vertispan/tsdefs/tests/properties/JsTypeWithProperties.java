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
package com.vertispan.tsdefs.tests.properties;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public class JsTypeWithProperties {

  public static String GLOBAL_EDITABLE = "value";
  public static final String GLOBAL_READONLY = "value";

  public int intProperty;
  public Integer integerProperty;
  public double doubleProperty;
  public Double doubleWrapperProperty;
  public boolean booleanProperty;
  public Boolean booleanWrapperProperty;
  public float floatProperty;
  public Float floatWrapperProperty;
  public Void voidType;
  public String stringProperty;
  public OtherJsType otherJsTypeProperty;

  @JsProperty(name = "differentName")
  public String withDifferentName;

  private String privateStringProperty;
  @JsProperty private String privateButExported;
  @JsIgnore public String jsIgnoredProperty;

  @JsProperty(namespace = "com.OtherNameSpace")
  public String propInOtherNamespace;
}
