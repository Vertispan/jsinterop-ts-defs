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

import static com.vertispan.tsdefs.Formatting.optionalAffix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParameterizedTsType extends TsType {

  private List<TsType> typeArguments = new ArrayList<>();

  public static ParameterizedTsType of(String name, String namespace, List<TsType> typeArguments) {
    return new ParameterizedTsType(name, namespace, typeArguments);
  }

  public ParameterizedTsType(String name, String namespace, List<TsType> typeArguments) {
    super(name, namespace);
    this.typeArguments.addAll(typeArguments);
  }

  @Override
  public String emit(String parentNamespace) {
    if (!typeArguments.isEmpty()) {
      return super.emit(parentNamespace) + emitTypeArgs(parentNamespace);
    }
    return super.emit(parentNamespace);
  }

  private String emitTypeArgs(String parentNamespace) {
    return typeArguments.stream()
        .map(tsType -> tsType.emit(parentNamespace))
        .collect(
            Collectors.joining(
                ",", optionalAffix("<", typeArguments), optionalAffix(">", typeArguments)));
  }
}
