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

import static com.vertispan.tsdefs.Formatting.resolveName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TsType {
  protected final String name;
  protected final String namespace;
  private List<TsType> bounds = new ArrayList<>();
  private boolean nullable;

  public static TsType of(String name, String namespace) {
    return new TsType(name, namespace);
  }

  public static TsType of(String name) {
    return new TsType(name, "");
  }

  public static TsType nullType() {
    return TsType.of("null");
  }

  public static TsType undefinedType() {
    return TsType.of("undefined");
  }

  public TsType(String name, String namespace) {
    this.name = name;
    this.namespace = namespace;
  }

  public String getName() {
    return name;
  }

  public String getNamespace() {
    return namespace;
  }

  public boolean isNullable() {
    return nullable;
  }

  public TsType addBounds(TsType tsType) {
    this.bounds.add(tsType);
    return this;
  }

  public String emit(String parentNamespace) {
    return emitType(parentNamespace);
  }

  protected String emitType(String parentNamespace) {
    if (namespace.isEmpty() || namespace.equals(parentNamespace)) {
      return resolveName(name) + emitBounds(parentNamespace);
    } else {
      return namespace + "." + resolveName(name) + emitBounds(parentNamespace);
    }
  }

  private String emitBounds(String parentNamespace) {
    if (bounds.isEmpty()) {
      return "";
    }
    return " extends "
        + bounds.stream()
            .map(tsType -> tsType.emit(parentNamespace))
            .collect(Collectors.joining(" & "));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TsType)) return false;
    TsType tsType = (TsType) o;
    return Objects.equals(getName(), tsType.getName())
        && Objects.equals(getNamespace(), tsType.getNamespace())
        && Objects.equals(bounds, tsType.bounds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName(), getNamespace());
  }
}
