/*
 * Copyright © 2026 Vertispan
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

import java.util.List;
import java.util.stream.Collectors;

/** {@code A&B}, a value of every listed type. */
public class TsIntersectionType extends TsType {
  private final List<TsType> types;

  private TsIntersectionType(List<TsType> types) {
    super("", "");
    this.types = types;
  }

  public static TsIntersectionType of(List<TsType> types) {
    return new TsIntersectionType(types);
  }

  @Override
  protected String emitType(String parentNamespace) {
    return types.stream()
        .map(
            type ->
                // & binds tighter than | and than a function type's return type.
                type instanceof TsUnionType
                        || type instanceof IsNullableTsType
                        || type instanceof TsInlinedFunctionType
                    ? "(" + type.emit(parentNamespace) + ")"
                    : type.emit(parentNamespace))
        .collect(Collectors.joining("&"));
  }
}
