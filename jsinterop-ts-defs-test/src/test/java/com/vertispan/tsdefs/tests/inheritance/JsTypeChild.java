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

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public class JsTypeChild extends JsTypeParent implements JsInterfaceOne, JsInterfaceTwo {

  // Should not export private member
  private String childPropertyA;
  // Should be exported as protected
  protected String childPropertyB;
  // Should be exported as fields
  public String childPropertyC;

  // Should be exported as childPropertyDRenamed
  @JsProperty(name = "childPropertyDRenamed")
  public String childPropertyD;

  // Should be exported as field
  public String parentPropertyE;

  // Should be exported
  public String childMethodOne() {
    return "";
  }

  // Should not export private member
  private void childMethodTwo() {}

  // Should be exported as protected
  protected void childMethodThree() {}

  // Should be exported as childMethodFourRenamed
  @JsMethod(name = "childMethodFourRenamed")
  public void childMethodFour() {}

  // Should be exported in parent only as protected
  protected void parentMethodFive() {}

  // Should be exported
  @Override
  public String interfaceOneMethodOne() {
    return null;
  }

  // Should not export JsIgnored method
  @Override
  public String interfaceOneIgnoredMethod() {
    return null;
  }

  // Should be exported as getter
  @Override
  public String getInterfaceOneProperty() {
    return null;
  }

  // Should be exported as setter
  @Override
  public void setInterfaceOneProperty(String parentPropertyF) {}

  // Should be exported
  @Override
  public String interfaceTwoMethodOne() {
    return null;
  }

  // Should not export ignored method
  @Override
  public String interfaceTwoIgnoredMethod() {
    return null;
  }

  // Should be exported as getter
  @Override
  public String getInterfaceTwoProperty() {
    return null;
  }

  // Should be exported as setter
  @Override
  public void setInterfaceTwoProperty(String parentPropertyF) {}

  // Should be exported as getter
  @Override
  public String getInterfaceOneGetterProperty() {
    return null;
  }

  // Should be exported as setter
  @Override
  public void setInterfaceOneSetterProperty(String parentPropertyF) {}
}
