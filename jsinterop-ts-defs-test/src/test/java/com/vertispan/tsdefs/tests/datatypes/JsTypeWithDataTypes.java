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
package com.vertispan.tsdefs.tests.datatypes;

import elemental2.core.JsArray;
import elemental2.core.JsMap;
import elemental2.core.JsNumber;
import elemental2.core.JsObject;
import elemental2.core.JsSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jsinterop.annotations.JsType;
import jsinterop.base.Any;
import jsinterop.base.JsPropertyMap;

@JsType
public class JsTypeWithDataTypes {

  public Object objectProperty;
  public JsObject jsObjectProperty;
  public String[] stringArrayProperty;
  public String[][] string2DArrayProperty;
  public List<String> stringListProperty;
  public Map<String, Double> mapProperty;
  public JsMap<String, Double> jsMapProperty;
  public Set<String> setProperty;
  public JsSet<String> jsSetProperty;
  public JsArray<String> jsArrayProperty;
  public JsPropertyMap<String> jsPropertyMapProperty;
  public Any anyProperty;
  public JsNumber jsNumberProperty;
  public JsPropertyMap<?>[] propertyMapArray;
  public JsPropertyMap<?>[][] propertyMap2dArray;
}
