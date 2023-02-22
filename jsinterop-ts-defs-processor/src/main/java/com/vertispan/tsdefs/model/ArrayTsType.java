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

public class ArrayTsType extends TsType {

  public static ArrayTsType of(TsType tsType) {
    return new ArrayTsType(tsType);
  }

  public ArrayTsType(TsType arrayComponent) {
    super(arrayComponent.name, arrayComponent.namespace);
  }

  @Override
  public String emitType(String parentNamespace) {
    return super.emitType(parentNamespace) + "[]";
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ArrayTsType) && super.equals(o);
  }
}
