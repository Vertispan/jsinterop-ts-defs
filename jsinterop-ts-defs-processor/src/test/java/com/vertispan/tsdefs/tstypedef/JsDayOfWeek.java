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
package com.vertispan.tsdefs.tstypedef;

import com.vertispan.tsdefs.annotations.TsTypeDef;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType(name = "DayOfWeek", namespace = "com.vertispan.calendar")
@SuppressWarnings("unusable-by-js")
@TsTypeDef(tsType = "string", name = "DayOfWeekType")
public class JsDayOfWeek {
  public static final String SUNDAY = "SUNDAY";
  public static final String MONDAY = "MONDAY";
  public static final String TUESDAY = "TUESDAY";
  public static final String WEDNESDAY = "WEDNESDAY";
  public static final String THURSDAY = "THURSDAY";
  public static final String FRIDAY = "FRIDAY";
  public static final String SATURDAY = "SATURDAY";

  @JsMethod
  public static String[] values() {
    return new String[] {};
  }
}
