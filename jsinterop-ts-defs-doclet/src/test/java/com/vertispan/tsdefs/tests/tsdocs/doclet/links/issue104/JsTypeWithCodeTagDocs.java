/*
 * Copyright © 2024 Vertispan
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
package com.vertispan.tsdefs.tests.tsdocs.doclet.links.issue104;

import jsinterop.annotations.JsType;

/**
 * This is a JsType to test {@code code snippet} in docs <i>example {@code code snippet a} in
 * tags</i> and we also test
 *
 * <ul>
 *   <li>example {@code code snippet b} in tags
 * </ul>
 *
 * {@code code snippet c}
 */
@JsType
public class JsTypeWithCodeTagDocs {

  /**
   * This is a field to test {@code code snippet} in docs <i>example {@code code snippet a} in
   * tags</i> and we also test
   *
   * <ul>
   *   <li>example {@code code snippet b} in tags
   * </ul>
   *
   * {@code code snippet c}
   */
  public String field;

  /**
   * This is a method to test {@code code snippet} in docs <i>example {@code code snippet a} in
   * tags</i> and we also test
   *
   * <ul>
   *   <li>example {@code code snippet b} in tags
   * </ul>
   *
   * {@code code snippet c}
   */
  public void doSomething() {
    return;
  }
}
