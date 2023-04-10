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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TsUnionType extends TsType {

  private List<TsType> tsTypes;

  public TsUnionType(List<TsType> tsTypes) {
    super("", "");
    this.tsTypes = tsTypes;
  }

  public static TsUnionType of(TsType... types) {
    return new TsUnionType(Arrays.asList(types));
  }

  @Override
  public String emit(String parentNamespace) {
    return tsTypes.stream()
        .map(tsType -> tsType.emit(parentNamespace, false))
        .collect(Collectors.joining("|"));
  }
}
