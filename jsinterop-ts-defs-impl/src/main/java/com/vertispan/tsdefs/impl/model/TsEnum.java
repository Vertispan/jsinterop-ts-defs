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

import com.vertispan.tsdefs.impl.builders.HasDocs;
import com.vertispan.tsdefs.impl.builders.HasFunctions;
import com.vertispan.tsdefs.impl.builders.HasNamespace;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class TsEnum implements HasNamespace {
  private final String name;
  private final String namespace;
  private TsCustomType type;
  private final Set<TsModifier> modifiers = new LinkedHashSet<>();
  private final Set<String> enumerations = new LinkedHashSet<>();
  private final Set<TsMethod> functions = new LinkedHashSet<>();
  private TsDoc tsDoc;
  private boolean deprecated;

  private TsEnum(String name, String namespace, TsCustomType type) {
    this.name = name;
    this.namespace = namespace;
    this.type = type;
  }

  public static TsEnumBuilder builder(String name, String namespace, TsCustomType type) {
    return new TsEnumBuilder(name, namespace, type);
  }

  public String getName() {
    return name;
  }

  public String getNamespace() {
    return namespace;
  }

  public TsType getType() {
    return type;
  }

  public String emit(String indent, String parentNamespace) {
    StringBuffer sb = new StringBuffer();
    if (nonNull(tsDoc)) {
      sb.append(tsDoc.emit(indent, deprecated));
    }
    sb.append(type.emitType(indent, parentNamespace));
    sb.append(NEW_LINE);

    TsClass.TsClassBuilder classBuilder =
        TsClass.builder(name, namespace).addModifiers(TsModifier.EXPORT).setDocs(TsDoc.empty());
    enumerations.stream()
        .map(
            enumeration ->
                TsProperty.builder(enumeration, TsType.of(type.name))
                    .setDocs(TsDoc.empty())
                    .addModifiers(TsModifier.STATIC, TsModifier.READONLY)
                    .build())
        .forEach(classBuilder::addProperty);

    functions.forEach(classBuilder::addFunction);

    sb.append(classBuilder.build().emit(indent, parentNamespace));

    return sb.toString();
  }

  public static class TsEnumBuilder implements HasDocs<TsEnumBuilder>, HasFunctions<TsEnumBuilder> {
    private final TsEnum tsEnum;

    private TsEnumBuilder(String name, String namespace, TsCustomType type) {
      this.tsEnum = new TsEnum(name, namespace, type);
    }

    public TsEnumBuilder addEnumeration(String name) {
      this.tsEnum.enumerations.add(name);
      return this;
    }

    public TsEnumBuilder addModifiers(TsModifier... modifiers) {
      this.tsEnum.modifiers.addAll(Arrays.asList(modifiers));
      return this;
    }

    @Override
    public TsEnumBuilder setDocs(TsDoc tsDoc) {
      this.tsEnum.tsDoc = tsDoc;
      return this;
    }

    public TsEnumBuilder setDeprecated(boolean deprecated) {
      this.tsEnum.deprecated = deprecated;
      return this;
    }

    @Override
    public TsEnumBuilder addFunction(TsMethod function) {
      this.tsEnum.functions.add(function);
      return this;
    }

    public TsEnum build() {
      return this.tsEnum;
    }
  }
}
