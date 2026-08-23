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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TsCustomType extends TsType {
  private final TsType referenceType;
  private final List<TsType> typeArguments;
  private final TsDoc tsDoc;

  private TsCustomType(String name, String namespace, TsType referenceType) {
    this(name, namespace, referenceType, TsDoc.empty());
  }

  private TsCustomType(String name, String namespace, TsType referenceType, TsDoc tsDoc) {
    super(name, namespace);
    this.referenceType = referenceType;
    this.tsDoc = tsDoc;
    this.typeArguments =
        referenceType instanceof TsUnionType
            ? ((TsUnionType) referenceType).getTypeArguments()
            : Collections.emptyList();
  }

  public static TsCustomType of(String name, String namespace, TsType referenceType) {
    return new TsCustomType(name, namespace, referenceType);
  }

  public static TsCustomType of(
      String name, String namespace, TsType referenceType, TsDoc tsDoc) {
    return new TsCustomType(name, namespace, referenceType, tsDoc);
  }

  public String emitType(String indent, String parentNamespace) {
    StringBuffer sb = new StringBuffer();
    sb.append(tsDoc.emit(indent, false));
    sb.append("type ");
    if (this.namespace.equals(parentNamespace) || namespace.isEmpty()) {
      sb.append(name);
    } else {
      sb.append(namespace + "." + name);
    }
    if (!typeArguments.isEmpty()) {
      sb.append(
          typeArguments.stream()
              .map(type -> type.emit(parentNamespace))
              .collect(java.util.stream.Collectors.joining(", ", "<", ">")));
    }
    sb.append(" = ");
    sb.append(referenceType.emit(parentNamespace));
    sb.append(";");

    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TsCustomType)) return false;
    if (!super.equals(o)) return false;
    TsCustomType that = (TsCustomType) o;
    return Objects.equals(referenceType, that.referenceType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), referenceType);
  }
}
