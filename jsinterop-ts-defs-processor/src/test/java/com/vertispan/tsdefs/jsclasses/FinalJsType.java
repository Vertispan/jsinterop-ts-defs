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
package com.vertispan.tsdefs.jsclasses;

import jsinterop.annotations.JsType;

/*
Test that final modifier will not produce a readonly modifier in type script
By just having this class the TS tests will fail to compile the result .d.ts file and produce an error if if has readonly modifier.
 */
@JsType
public final class FinalJsType {
  public String property;

  public final void doSomething(final String name) {}
}
