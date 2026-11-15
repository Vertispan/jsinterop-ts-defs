/*
 * Copyright © 2026 Vertispan
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
package com.vertispan.tsdefs.tests.tsdocs.doclet.links.labels;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(namespace = "probe", name = "Target")
public class JsTarget {
  public static final double BAR = -2;

  public JsTarget() {}

  @JsMethod(name = "go")
  public void start() {}

  @JsProperty
  public double getSize() {
    return 0;
  }

  @JsProperty
  public void setSize(double size) {}

  /** A declared name starting with "set" is used as written, not stripped. */
  @JsProperty(name = "settings")
  public double getSettings() {
    return 0;
  }

  private void hidden() {}

  /** Emitted outside Target, though GWT nests it inside: links to it stay unlabeled. */
  @JsType
  public static class Inner {
    public void go() {}
  }

  /** Same class: method {@link #start()}, field {@link #BAR}. */
  @JsMethod
  public void self() {}
}
