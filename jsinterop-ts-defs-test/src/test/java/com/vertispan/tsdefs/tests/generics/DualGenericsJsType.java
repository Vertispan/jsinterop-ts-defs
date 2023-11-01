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
package com.vertispan.tsdefs.tests.generics;

import jsinterop.annotations.JsType;

@JsType
public class DualGenericsJsType<T, C> {

  public T propertyOfT;
  public C propertyOfC;

  public T takesNothingReturnT() {
    return null;
  }

  public T takeTReturnT(T parameterOfT) {
    return null;
  }

  public C takesNothingReturnC() {
    return null;
  }

  public C takeCReturnC(C parameterOfC) {
    return null;
  }

  public T takesCReturnT(C propertyOfC) {
    return null;
  }

  public C takesTReturnC(T propertyOfT) {
    return null;
  }
}
