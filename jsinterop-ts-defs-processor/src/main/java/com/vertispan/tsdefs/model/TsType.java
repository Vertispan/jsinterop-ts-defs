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
import java.util.stream.Collectors;

public class TsType {
  protected final String name;
  protected final String namespace;
  private List<TsType> bounds = new ArrayList<>();

  public static TsType of(String name, String namespace) {
    return new TsType(name, namespace);
  }

  public static TsType of(String name) {
    return new TsType(name, "");
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

  public TsType addBounds(TsType tsType) {
    this.bounds.add(tsType);
    return this;
  }

  public String emit(String parentNamespace) {
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
}
