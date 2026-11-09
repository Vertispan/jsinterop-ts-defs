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
package com.vertispan.tsdefs.tests.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;

/** Properties and a bounded method type parameter that use the interface's T. */
@TsInterface
public interface Settable<T> {
  @JsProperty
  T getValue();

  @JsProperty
  void setValue(T value);

  @JsProperty
  void setTarget(T target);

  @JsMethod
  <R extends T> R narrow();
}
