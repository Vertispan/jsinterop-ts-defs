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
package com.vertispan.tsdefs.tests.inheritance;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public class JsTypeParent {

  private String parentPropertyA;
  protected String parentPropertyB;
  public String parentPropertyC;

  @JsProperty(name = "parentPropertyDRenamed")
  public String parentPropertyD;

  private String parentPropertyE;
  private String parentPropertyF;
  @JsIgnore public String parentPropertyG;

  public String parentMethodOne() {
    return "";
  }

  private void parentMethodTwo() {}

  protected void parentMethodThree() {}

  @JsMethod(name = "parentMethodFourRenamed")
  public void parentMethodFour() {}

  protected void parentMethodFive() {}

  @JsIgnore
  public String parentIgnoredMethod() {
    return "";
  }

  @JsProperty
  public String getParentPropertyF() {
    return parentPropertyF;
  }

  @JsProperty
  public void setParentPropertyF(String parentPropertyF) {
    this.parentPropertyF = parentPropertyF;
  }
}
