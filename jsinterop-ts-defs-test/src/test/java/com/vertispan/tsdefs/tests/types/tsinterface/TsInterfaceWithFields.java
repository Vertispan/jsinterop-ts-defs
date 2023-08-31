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
package com.vertispan.tsdefs.tests.types.tsinterface;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsProperty;

@TsInterface
public class TsInterfaceWithFields {

  @JsProperty public String name;
  @JsProperty private String otherName;
  @JsProperty public final String prop;
  private String otherProp;

  private String propx;

  public TsInterfaceWithFields(String prop) {
    this.prop = prop;
  }

  @JsProperty(name = "id")
  public void setId(String id) {}

  public String getOtherProp() {
    return otherProp;
  }

  public void setOtherProp(String otherProp) {
    this.otherProp = otherProp;
  }

  @JsProperty
  public String getPropx() {
    return propx;
  }

  @JsProperty
  public void setPropx(String propx) {
    this.propx = propx;
  }
}
