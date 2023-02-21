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
package com.vertispan.tsdefs.settergetter;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public class JsTypeWithSettersAndGetters {
  // Should not be exported
  private String propertyA;
  // Exported as a property because it has JsProperty setter and getter
  private String propertyC;
  private String propertyE;
  private String propertyF;

  private final String propertyH;

  public JsTypeWithSettersAndGetters(String propertyH) {
    this.propertyH = propertyH;
  }

  // Exported as normal method
  public String getPropertyA() {
    return propertyA;
  }
  // Exported as normal method
  public void setPropertyA(String propertyA) {
    this.propertyA = propertyA;
  }
  // Exported as normal method
  public String getPropertyB() {
    return "";
  }
  // Exported as normal method
  public void setPropertyB(String propertyB) {}

  @JsProperty(name = "propertyC")
  public String getPropertyC() {
    return propertyC;
  }

  @JsProperty(name = "propertyC")
  public void setPropertyC(String propertyC) {
    this.propertyC = propertyC;
  }

  // Exported as a property because it has JsProperty getter
  @JsProperty(name = "propertyD")
  public String getPropertyD() {
    return "";
  }
  // Exported as a property because it has JsProperty setter
  @JsProperty(name = "propertyD")
  public void setPropertyD(String propertyC) {}

  // Exported as normal method
  public String getPropertyE() {
    return propertyE;
  }

  // Exported as normal method
  public void setPropertyF(String propertyF) {
    this.propertyF = propertyF;
  }

  // Should be exported as TS get
  @JsProperty(name = "renamedPropertyG")
  public String getPropertyG() {
    return "";
  }

  // Exported as normal method
  public String getPropertyH() {
    return propertyH;
  }

  // propertyH is final
  // With this I can trick it to allow setters for final fields
  // Tested this with GWT and JsInterop and there was no error
  // Should we error out in such case or not
  @JsProperty(name = "propertyH")
  public void setFinalPropertyH(String h) {}

  @JsProperty(name = "isHasFlag")
  public boolean isHasFlag() {
    return true;
  }

  @JsProperty(name = "getFlag")
  public String flag() {
    return "";
  }

  @JsProperty(name = "setFlag")
  public void putFlag(String flag) {}
}
