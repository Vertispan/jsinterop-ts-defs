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

import com.vertispan.tsdefs.builders.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TsInterface implements IsType {
  private String name;
  private String namespace;
  private List<TsModifier> modifiers = new ArrayList<>();
  private List<TsProperty> properties = new ArrayList<>();
  private List<TsMethod> jsFunctions = new ArrayList<>();
  private List<TsType> typeArguments = new ArrayList<>();
  private List<TsInterface> interfaces = new ArrayList<>();

  private TsDoc tsDoc;
  private boolean deprecated;

  private TsInterfaceBuilder builder;

  public TsInterfaceBuilder getBuilder() {
    return builder;
  }

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

    sb.append(" {");

    sb.append(
        properties.stream()
            .map(property -> property.emit(indent + INDENT, END_LINE, namespace))
            .collect(Collectors.joining(NEW_LINE, optionalln(properties), optionalln(properties))));

    sb.append(
        jsFunctions.stream()
            .map(function -> function.emit(indent + INDENT, namespace))
            .collect(
                Collectors.joining(NEW_LINE, optionalln(jsFunctions), optionalln(jsFunctions))));

    sb.append(indent);
    sb.append("}");

    return sb.toString();
  }

  @Override
  public String getNamespace() {
    return this.namespace;
  }

  @Override
  public String getName() {
    return name;
  }

  public String emitType(String parentNamespace) {
    StringBuffer sb = new StringBuffer();
    if (getNamespace().equals(parentNamespace) || namespace.isEmpty()) {
      sb.append(name);
    } else {
      sb.append(namespace + "." + name);
    }
    if (!typeArguments.isEmpty()) {
      sb.append(
          typeArguments.stream()
              .map(tsType -> tsType.emit(namespace))
              .collect(Collectors.joining(", ", "<", ">")));
    }
    return sb.toString();
  }

  public static TsInterfaceBuilder builder(String name, String namespace) {
    return new TsInterfaceBuilder(name, namespace);
  }

  private TsInterface(String name, String namespace) {
    this.name = name;
    this.namespace = namespace;
  }

  public static class TsInterfaceBuilder
      implements HasMembers<TsInterfaceBuilder>,
          HasTypeArguments<TsInterfaceBuilder>,
          HasInterfaces<TsInterfaceBuilder>,
          HasDocs<TsInterfaceBuilder> {

    private TsInterface tsInterface;

    private TsInterfaceBuilder(String name, String namespace) {
      this.tsInterface = new TsInterface(name, namespace);
      this.tsInterface.builder = this;
    }

    public TsInterfaceBuilder addModifiers(TsModifier... modifiers) {
      this.tsInterface.modifiers.addAll(Arrays.asList(modifiers));
      return this;
    }

    @Override
    public TsInterfaceBuilder addFunction(TsMethod function) {
      this.tsInterface.jsFunctions.add(function);
      return this;
    }

    @Override
    public TsInterfaceBuilder addProperty(TsProperty property) {
      this.tsInterface.properties.add(property);
      return this;
    }

    @Override
    public TsInterfaceBuilder addTypeArgument(TsType typeArgument) {
      this.tsInterface.typeArguments.add(typeArgument);
      return this;
    }

    @Override
    public TsInterfaceBuilder addInterface(TsInterface tsInterface) {
      this.tsInterface.interfaces.add(tsInterface);
      return this;
    }

    @Override
    public TsInterfaceBuilder setDocs(TsDoc tsDoc) {
      this.tsInterface.tsDoc = tsDoc;
      return this;
    }

    public TsInterfaceBuilder setDeprecated(boolean deprecated) {
      this.tsInterface.deprecated = deprecated;
      return this;
    }

    public TsInterface build() {
      return this.tsInterface;
    }
  }
}
