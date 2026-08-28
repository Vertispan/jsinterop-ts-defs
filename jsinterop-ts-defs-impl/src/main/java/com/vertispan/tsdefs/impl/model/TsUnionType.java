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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TsUnionType extends TsType {

  private final Set<TsType> tsTypes;
  private final boolean anonymous;
  private final List<TsType> typeArguments;

  public TsUnionType(boolean anonymous, Collection<TsType> tsTypes) {
    this("", "", anonymous, Collections.emptyList(), tsTypes);
  }

  public TsUnionType(boolean anonymous, TsType... tsTypes) {
    this("", "", anonymous, Collections.emptyList(), new LinkedHashSet<>(Arrays.asList(tsTypes)));
  }

  public TsUnionType(String name, String namespace, boolean anonymous, Collection<TsType> tsTypes) {
    this(name, namespace, anonymous, Collections.emptyList(), tsTypes);
  }

  public TsUnionType(
      String name,
      String namespace,
      boolean anonymous,
      List<TsType> typeArguments,
      Collection<TsType> tsTypes) {
    super(name, namespace);
    this.anonymous = anonymous;
    this.typeArguments = typeArguments;

    this.tsTypes =
        tsTypes.stream()
            .flatMap(
                type -> {
                  if (type instanceof TsUnionType && ((TsUnionType) type).anonymous) {
                    return ((TsUnionType) type).tsTypes.stream();
                  }
                  return Stream.of(type);
                })
            .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public static TsUnionType of(String name, String namespace, boolean anonymous, TsType... types) {
    return new TsUnionType(name, namespace, anonymous, new LinkedHashSet<>(Arrays.asList(types)));
  }

  public static TsUnionType of(
      String name, String namespace, boolean anonymous, Set<TsType> types) {
    return new TsUnionType(name, namespace, anonymous, types);
  }

  public static TsUnionType of(
      String name,
      String namespace,
      boolean anonymous,
      List<TsType> typeArguments,
      Set<TsType> types) {
    return new TsUnionType(name, namespace, anonymous, typeArguments, types);
  }

  public static TsUnionType of(boolean anonymous, Set<TsType> types) {
    return new TsUnionType(anonymous, types);
  }

  public static TsUnionType of(boolean anonymous, TsType... types) {
    return new TsUnionType(anonymous, types);
  }

  public List<TsType> getTypeArguments() {
    return typeArguments;
  }

  @Override
  public String emit(String parentNamespace) {
    if (!anonymous) {
      return super.emit(parentNamespace);
    }
    return tsTypes.stream()
        .map(tsType -> tsType.emit(parentNamespace))
        .collect(Collectors.joining("|"));
  }
}
