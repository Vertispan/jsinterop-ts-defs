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
import static java.util.Objects.nonNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TsDoc {
  private final String docs;

  public static TsDoc of(String docs) {
    return new TsDoc(docs);
  }

  public static TsDoc empty() {
    return TsDoc.of("");
  }

  private TsDoc(String docs) {
    this.docs = docs;
  }

  public String emit(String indent, boolean deprecated) {
    StringBuffer sb = new StringBuffer();
    if (nonNull(docs) && !docs.trim().isEmpty()) {
      sb.append(indent);
      sb.append("/**");
      sb.append(
          Arrays.stream(docs.split("\\r?\\n|\\r"))
              .map(line -> NEW_LINE + indent + "*" + line)
              .collect(Collectors.joining()));
      if (deprecated && !docs.contains("@deprecated")) {
        sb.append(NEW_LINE);
        sb.append(indent);
        sb.append("* @deprecated");
      }
      sb.append(NEW_LINE);
      sb.append(indent);
      sb.append("*/");
      sb.append(NEW_LINE);
      sb.append(indent);
    } else if (deprecated) {
      sb.append(indent);
      sb.append("/**");
      sb.append(NEW_LINE);
      sb.append(indent);
      sb.append("* @deprecated");
      sb.append(NEW_LINE);
      sb.append(indent);
      sb.append("*/");
      sb.append(NEW_LINE);
      sb.append(indent);
    } else {
      sb.append(indent);
    }

    return sb.toString();
  }
}
