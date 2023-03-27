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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TsUnionType extends TsType {

  private final Set<TsType> tsTypes = new LinkedHashSet<>();

  public TsUnionType(Set<TsType> tsTypes) {
    super("", "");

    tsTypes.forEach(
        tsType -> {
          if (tsType instanceof TsUnionType) {
            this.tsTypes.addAll(((TsUnionType) tsType).tsTypes);
          } else {
            this.tsTypes.add(tsType);
          }
        });
  }

  public static TsUnionType of(TsType... types) {
    return new TsUnionType(new HashSet<>(Arrays.asList(types)));
  }

  public static TsUnionType of(Set<TsType> types) {
    return new TsUnionType(types);
  }

  @Override
  public String emit(String parentNamespace) {
    return tsTypes.stream()
        .map(tsType -> tsType.emit(parentNamespace))
        .collect(Collectors.joining("|"));
  }
}
