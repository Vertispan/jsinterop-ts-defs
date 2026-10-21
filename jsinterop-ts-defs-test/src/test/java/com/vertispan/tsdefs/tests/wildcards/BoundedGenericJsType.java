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
package com.vertispan.tsdefs.tests.wildcards;

import com.vertispan.tsdefs.tests.generics.GenericInterfaceOne;
import com.vertispan.tsdefs.tests.generics.GenericJsType;
import jsinterop.annotations.JsType;

/** Same as {@link BoundedBox} but for a class: superclass and implemented interface. */
@JsType
public class BoundedGenericJsType<T extends Item> extends GenericJsType<T>
    implements GenericInterfaceOne<T> {
  @Override
  public T genericInterfaceOneMethod() {
    return null;
  }

  @Override
  public T getGenericPropertyOne() {
    return null;
  }

  @Override
  public void setGenericPropertyOne(T genericPropertyOne) {}
}
