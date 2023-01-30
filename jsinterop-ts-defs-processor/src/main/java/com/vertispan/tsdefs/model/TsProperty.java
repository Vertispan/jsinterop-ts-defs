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
import static java.util.Objects.nonNull;

import com.vertispan.tsdefs.builders.Deprecation;
import com.vertispan.tsdefs.builders.HasDocs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TsProperty {
  private final String name;
  private final TsType type;
  private TsDoc tsDoc;
  private boolean jsOptional;
  private boolean deprecated;
  private List<TsModifier> modifiers = new ArrayList<>();

  private TsProperty(String name, TsType type) {
    this.name = name;
    this.type = type;
  }

  public String emit(String indent, String ending, String parentNamespace) {
    StringBuffer sb = new StringBuffer();

    if (nonNull(tsDoc)) {
      sb.append(tsDoc.emit(indent, deprecated));
    } else if (deprecated) {
      sb.append(Deprecation.emit(indent));
    }
    sb.append(modifiers.stream().map(TsModifier::emit).collect(Collectors.joining("", "", "")));

    sb.append(resolveName(name));
    if (jsOptional) {
      sb.append("?");
    }
    sb.append(":");
    sb.append(type.emit(parentNamespace));
    sb.append(ending);

    return sb.toString();
  }

  public static TsPropertyBuilder builder(String name, TsType type) {
    return new TsPropertyBuilder(name, type);
  }

  public static class TsPropertyBuilder implements HasDocs<TsPropertyBuilder> {

    private final TsProperty property;

    private TsPropertyBuilder(String name, TsType type) {
      this.property = new TsProperty(name, type);
    }

    public TsPropertyBuilder addModifiers(TsModifier... modifiers) {
      this.property.modifiers.addAll(Arrays.asList(modifiers));
      return this;
    }

    @Override
    public TsPropertyBuilder setDocs(TsDoc tsDoc) {
      this.property.tsDoc = tsDoc;
      return this;
    }

    public TsPropertyBuilder setOptional(boolean optional) {
      this.property.jsOptional = optional;
      return this;
    }

    public TsPropertyBuilder setDeprecated(boolean deprecated) {
      this.property.deprecated = deprecated;
      return this;
    }

    public TsProperty build() {
      return property;
    }
  }
}
