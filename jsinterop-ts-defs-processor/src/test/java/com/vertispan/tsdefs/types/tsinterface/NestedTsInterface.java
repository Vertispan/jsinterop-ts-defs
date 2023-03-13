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
package com.vertispan.tsdefs.types.tsinterface;

import com.vertispan.tsdefs.annotations.TsInterface;
import com.vertispan.tsdefs.annotations.TsName;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;

@TsName(namespace = "com.vertispan", name = "ParentA")
public class NestedTsInterface {

  @JsMethod
  public String doSomething() {
    return "";
  }

  @TsInterface
  @TsName(namespace = "com.vertispan")
  public static class ParentB {
    @JsProperty
    public double getOffset() {
      return 0;
    }

    @TsInterface
    @TsName(name = "TreeRow", namespace = "com.vertispan")
    public static class Child {
      @JsProperty(name = "isExpanded")
      public boolean isExpanded() {
        return true;
      }

      @JsProperty(name = "hasChildren")
      public boolean hasChildren() {
        return true;
      }

      @JsProperty(name = "depth")
      public int depth() {
        return 0;
      }
    }
  }
}
