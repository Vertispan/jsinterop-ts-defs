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

import static com.vertispan.tsdefs.Formatting.INDENT;
import static com.vertispan.tsdefs.Formatting.NEW_LINE;
import static java.util.Objects.nonNull;

import com.vertispan.tsdefs.builders.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TsFunction implements HasNamespace {

  private String name;
  private String namespace;
  private List<TsModifier> modifiers = new ArrayList<>();
  private TsMethod jsMethod;
  private List<TsType> typeArguments = new ArrayList<>();
  private List<TsInterface> interfaces = new ArrayList<>();

  private TsDoc tsDoc;
  private boolean deprecated;

  public String emit(String indent, String parentNamespace) {
    StringBuffer sb = new StringBuffer();
    if (nonNull(tsDoc)) {
      sb.append(tsDoc.emit(indent, deprecated));
    }
    sb.append(modifiers.stream().map(TsModifier::emit).collect(Collectors.joining(" ")));
    sb.append("interface ");
    sb.append(emitType(parentNamespace));

    if (!interfaces.isEmpty()) {
      sb.append(" extends ");
      sb.append(
          interfaces.stream()
              .map(tsInterface -> tsInterface.emitType(parentNamespace))
              .collect(Collectors.joining(", ")));
    }

    sb.append("{");
    sb.append(NEW_LINE);
    sb.append(jsMethod.emit(indent + INDENT, namespace));
    sb.append(NEW_LINE);
    sb.append(indent);
    sb.append("}");

    return sb.toString();
  }

  @Override
  public String getNamespace() {
    return this.namespace;
  }

  public String emitType(String parentNamespace) {
    // We will emit JsFunctions without a namespace since this will make no difference from type
    // script as log as the parameters are the same
    StringBuffer sb = new StringBuffer();
    sb.append(name);
    if (!typeArguments.isEmpty()) {
      sb.append(
          typeArguments.stream()
              .map(tsType -> tsType.emit(namespace))
              .collect(Collectors.joining(", ", "<", ">")));
    }
    return sb.toString();
  }

  public static TsFunctionBuilder builder(String name, String namespace) {
    return new TsFunctionBuilder(name, namespace);
  }

  private TsFunction(String name, String namespace) {
    this.name = name;
    this.namespace = namespace;
  }

  public static class TsFunctionBuilder
      implements HasTypeArguments<TsFunctionBuilder>,
          HasInterfaces<TsFunctionBuilder>,
          HasDocs<TsFunctionBuilder> {

    private TsFunction tsFunction;

    private TsFunctionBuilder(String name, String namespace) {
      this.tsFunction = new TsFunction(name, namespace);
    }

    public TsFunctionBuilder addModifiers(TsModifier... modifiers) {
      this.tsFunction.modifiers.addAll(Arrays.asList(modifiers));
      return this;
    }

    public TsFunctionBuilder setFunction(TsMethod method) {
      this.tsFunction.jsMethod = method;
      return this;
    }

    @Override
    public TsFunctionBuilder addTypeArgument(TsType typeArgument) {
      this.tsFunction.typeArguments.add(typeArgument);
      return this;
    }

    @Override
    public TsFunctionBuilder addInterface(TsInterface tsInterface) {
      this.tsFunction.interfaces.add(tsInterface);
      return this;
    }

    @Override
    public TsFunctionBuilder setDocs(TsDoc tsDoc) {
      this.tsFunction.tsDoc = tsDoc;
      return this;
    }

    public TsFunctionBuilder setDeprecated(boolean deprecated) {
      this.tsFunction.deprecated = deprecated;
      return this;
    }

    public TsFunction build() {
      return this.tsFunction;
    }
  }
}
