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

import elemental2.promise.Promise;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType(namespace = "probe", name = "Source")
public class Source {
  /**
   * Method {@link JsTarget#start()}, parameters {@link Globals#scale(double)}, field {@link
   * JsTarget#BAR}, property {@link JsTarget#getSize()}, class {@link JsTarget}, global {@link
   * Globals#all()}, labeled {@link JsTarget#start() the starter}.
   */
  @JsMethod
  public void links() {}

  /** Accessor with a declared name: {@link JsTarget#getSettings()}. */
  @JsMethod
  public void declaredAccessor() {}

  /**
   * Not emitted: {@link String}, {@link java.util.List#size()}, {@link Promise}, {@link
   * JsTarget#hidden()}.
   */
  @JsMethod
  public void notEmitted() {}

  /** Constructor: {@link JsTarget#JsTarget()}. */
  @JsMethod
  public void constructorLink() {}

  /** Label with a tag: {@link JsTarget#start() the {@code go} call}. */
  @JsMethod
  public void taggedLabel() {}
}
