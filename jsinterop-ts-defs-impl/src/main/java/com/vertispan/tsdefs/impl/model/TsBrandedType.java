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

import com.vertispan.tsdefs.impl.builders.HasDocs;
import com.vertispan.tsdefs.impl.builders.HasFunctions;
import com.vertispan.tsdefs.impl.builders.HasNamespace;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class TsBrandedType implements HasNamespace {
  private final String name;
  private final String namespace;
  private TsType type;
  private final Set<TsModifier> modifiers = new LinkedHashSet<>();
  private final Set<String> enumerations = new LinkedHashSet<>();
  private final Set<TsMethod> functions = new LinkedHashSet<>();
  private TsDoc tsDoc;
  private boolean deprecated;

  private TsBrandedType(String name, String namespace, TsType type) {
    this.name = name;
    this.namespace = namespace;
    this.type = type;
  }

  public static TsBrandedType.TsBrandedTypeBuilder builder(
      String name, String namespace, TsType type) {
    return new TsBrandedType.TsBrandedTypeBuilder(name, namespace, type);
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

    TsBrand tsBrand = new TsBrand(name, namespace);
    sb.append(tsBrand.emit(indent));
    sb.append(NEW_LINE);

    TsClass.TsClassBuilder classBuilder =
        TsClass.builder(name, namespace).addModifiers(TsModifier.EXPORT).setDocs(tsDoc);
    enumerations.stream()
        .map(
            enumeration ->
                TsProperty.builder(enumeration, tsBrand.getType())
                    .setDocs(TsDoc.empty())
                    .addModifiers(TsModifier.STATIC, TsModifier.READONLY)
                    .build())
        .forEach(classBuilder::addProperty);

    functions.forEach(classBuilder::addFunction);

    sb.append(classBuilder.build().emit(indent, parentNamespace));

    return sb.toString();
  }

  public static class TsBrandedTypeBuilder
      implements HasDocs<TsBrandedType.TsBrandedTypeBuilder>,
          HasFunctions<TsBrandedType.TsBrandedTypeBuilder> {
    private final TsBrandedType tsEnum;

    private TsBrandedTypeBuilder(String name, String namespace, TsType type) {
      this.tsEnum = new TsBrandedType(name, namespace, type);
    }

    public TsBrandedType.TsBrandedTypeBuilder addEnumeration(String name) {
      this.tsEnum.enumerations.add(name);
      return this;
    }

    public TsBrandedType.TsBrandedTypeBuilder addModifiers(TsModifier... modifiers) {
      this.tsEnum.modifiers.addAll(Arrays.asList(modifiers));
      return this;
    }

    @Override
    public TsBrandedType.TsBrandedTypeBuilder setDocs(TsDoc tsDoc) {
      this.tsEnum.tsDoc = tsDoc;
      return this;
    }

    public TsBrandedType.TsBrandedTypeBuilder setDeprecated(boolean deprecated) {
      this.tsEnum.deprecated = deprecated;
      return this;
    }

    @Override
    public TsBrandedType.TsBrandedTypeBuilder addFunction(TsMethod function) {
      this.tsEnum.functions.add(function);
      return this;
    }

    public TsBrandedType build() {
      return this.tsEnum;
    }
  }
}
