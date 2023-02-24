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

import static com.vertispan.tsdefs.Formatting.*;
import static java.util.Objects.nonNull;

import com.vertispan.tsdefs.builders.HasDocs;
import com.vertispan.tsdefs.builders.HasProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TsMethod {
  private final String name;
  private final TsType returnType;
  private final List<TsProperty> parameters = new ArrayList<>();
  private final List<TsModifier> modifiers = new ArrayList<>();
  private TsDoc tsDoc;
  private boolean deprecated;

  public String getName() {
    return name;
  }

  private TsMethod(String name, TsType returnType) {
    this.name = name;
    this.returnType = returnType;
  }

  private TsMethod(String name) {
    this.name = name;
    this.returnType = new NoneTsType();
  }

  public static TsMethodBuilder builder(String name) {
    return new TsMethodBuilder(name);
  }

  public static TsMethodBuilder builder(String name, TsType returnType) {
    return new TsMethodBuilder(name, returnType);
  }

  public String emit(String indent, String parentNamespace) {
    StringBuffer sb = new StringBuffer();

    if (nonNull(tsDoc)) {
      sb.append(tsDoc.emit(indent, deprecated));
    }
    sb.append(
        modifiers.stream()
            .filter(tsModifier -> TsModifier.READONLY != tsModifier)
            .map(TsModifier::emit)
            .collect(Collectors.joining(NONE)));

    sb.append(name);
    sb.append("(");
    sb.append(
        parameters.stream()
            .map(property -> property.emitAsParameter(parentNamespace))
            .collect(Collectors.joining(COMMA)));

    sb.append(")");
    if (!(returnType instanceof NoneTsType)) {
      sb.append(":");
      sb.append(returnType.emit(parentNamespace));
    }
    sb.append(END_LINE);

    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TsMethod)) return false;
    TsMethod tsMethod = (TsMethod) o;
    return Objects.equals(getName(), tsMethod.getName())
        && Objects.equals(returnType, tsMethod.returnType)
        && parameters.equals(tsMethod.parameters)
        && modifiers.equals(tsMethod.modifiers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName(), returnType, parameters, modifiers);
  }

  public static class TsMethodBuilder
      implements HasProperties<TsMethodBuilder>, HasDocs<TsMethodBuilder> {
    private final TsMethod method;

    private TsMethodBuilder(String name, TsType returnType) {
      this.method = new TsMethod(name, returnType);
    }

    private TsMethodBuilder(String name) {
      this.method = new TsMethod(name);
    }

    @Override
    public TsMethodBuilder addProperty(TsProperty property) {
      this.method.parameters.add(property);
      return this;
    }

    public TsMethodBuilder addModifiers(TsModifier... modifiers) {
      this.method.modifiers.addAll(Arrays.asList(modifiers));
      return this;
    }

    @Override
    public TsMethodBuilder setDocs(TsDoc tsDoc) {
      this.method.tsDoc = tsDoc;
      return this;
    }

    public TsMethodBuilder setDeprecated(boolean deprecated) {
      this.method.deprecated = deprecated;
      return this;
    }

    public TsMethod build() {
      return this.method;
    }
  }
}
