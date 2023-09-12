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
package com.vertispan.tsdefs.impl.model;

public class TsNullableType extends TsType implements IsNullableTsType {

  private final TsType type;
  private boolean undefined = true;

  private TsNullableType(TsType type) {
    super(type.name, type.namespace);
    this.type = type;
  }

  public static TsNullableType of(TsType tsType) {
    if (tsType instanceof TsNullableType) {
      return (TsNullableType) tsType;
    }
    return new TsNullableType(tsType);
  }

  @Override
  public String emit(String parentNamespace) {
    if (undefined) {
      return TsUnionType.of(type, TsType.nullType(), TsType.undefinedType()).emit(parentNamespace);
    }
    return TsUnionType.of(type, TsType.nullType()).emit(parentNamespace);
  }

  @Override
  public void setUndefined(boolean undefined) {
    this.undefined = undefined;
  }
}
