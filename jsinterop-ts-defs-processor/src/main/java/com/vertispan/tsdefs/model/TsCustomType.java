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

public class TsCustomType extends TsType {
  private final TsType referenceType;

  private TsCustomType(String name, String namespace, TsType referenceType) {
    super(name, namespace);
    this.referenceType = referenceType;
  }

  public static TsCustomType of(String name, String namespace, TsType referenceType) {
    return new TsCustomType(name, namespace, referenceType);
  }

  public String emitType(String indent, String parentNamespace) {
    StringBuffer sb = new StringBuffer();
    sb.append("type ");
    if (this.namespace.equals(parentNamespace) || namespace.isEmpty()) {
      sb.append(name);
    } else {
      sb.append(namespace + "." + name);
    }
    sb.append(" = ");
    sb.append(referenceType.emit(parentNamespace));
    sb.append(";");

    return sb.toString();
  }
}
