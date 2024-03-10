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

import static com.vertispan.tsdefs.impl.Formatting.NEW_LINE;

public class TsBrand {
  private final String name;
  private final String namespace;
  private final TsType type;

  public TsBrand(String name, String namespace) {
    this.name = name;
    this.namespace = namespace;
    this.type = new TsType(name + "Type", namespace);
  }

  public String emit(String indent) {
    StringBuffer sb = new StringBuffer();
    sb.append(indent);
    sb.append("const " + name + "__brand: unique symbol");
    sb.append(NEW_LINE);
    sb.append(indent);
    sb.append("type Brand<B> = { [" + name + "__brand]: B }");
    sb.append(NEW_LINE);
    sb.append(indent);
    sb.append("export type Branded<T, B> = T & Brand<B>");
    sb.append(NEW_LINE);
    sb.append(indent);
    sb.append("type " + name + "Type = Branded<string, \"" + name + "\">;");
    sb.append(NEW_LINE);
    sb.append(indent);
    return sb.toString();
  }

  public TsType getType() {
    return this.type;
  }
}
