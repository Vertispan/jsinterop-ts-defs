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
package com.vertispan.tsdefs.model;

public class Array2dTsType extends ArrayTsType {

  public static Array2dTsType of(TsType tsType) {
    return new Array2dTsType(tsType);
  }

  public Array2dTsType(TsType arrayComponent) {
    super(arrayComponent);
  }

  @Override
  public String emitType(String parentNamespace) {
    return super.emitType(parentNamespace) + "[]";
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Array2dTsType) && super.equals(o);
  }
}
