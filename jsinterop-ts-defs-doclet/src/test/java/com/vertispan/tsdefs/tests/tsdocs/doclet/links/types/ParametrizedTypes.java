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
package com.vertispan.tsdefs.tests.tsdocs.doclet.links.types;

import elemental2.promise.Promise;
import jsinterop.annotations.JsType;

/**
 * Represents a class that demonstrates the use of parametrized types.
 *
 * @param <T> the type of parameter this class operates on
 */
@JsType
public class ParametrizedTypes<T> {

  /**
   * Retrieves a Promise containing the provided parameter.
   *
   * @param param1 a {@link Promise} of type T
   * @param param2 an additional string parameter
   * @return a {@link Promise} containing the value of type T
   */
  public Promise<T> getParameter(Promise<T> param1, String param2) {
    // Implementation here
    return null;
  }
}
