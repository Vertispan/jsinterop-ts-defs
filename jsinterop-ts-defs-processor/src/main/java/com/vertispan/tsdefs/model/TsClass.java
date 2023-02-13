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

public class TsClass implements IsType {

  private final String name;
  private final String namespace;
  private TsClass superClass;
  private List<TsInterface> interfaces = new ArrayList<>();
  private List<TsModifier> modifiers = new ArrayList<>();
  private List<TsProperty> properties = new ArrayList<>();
  private TsConstructor constructor;
  private List<TsMethod> functions = new ArrayList<>();
  private List<TsType> typeArguments = new ArrayList<>();
  private TsDoc tsDoc;
  private boolean deprecated;

  private boolean emitProtectedConstructor;

  private TsClassBuilder builder;

  public TsClassBuilder getBuilder() {
    return builder;
  }

  private TsClass(String name, String namespace) {
    this.name = name;
    this.namespace = namespace;
  }

  public void mergeWith(TsClass other) {
    this.interfaces.addAll(other.interfaces);
    this.typeArguments.addAll(other.typeArguments);
    this.functions.addAll(other.functions);
    this.properties.addAll(other.properties);
  }

  @Override
  public String getNamespace() {
    return this.namespace;
  }

  @Override
  public String getName() {
    return name;
  }

  public static TsClassBuilder builder(String name, String namespace) {
    return new TsClassBuilder(name, namespace);
  }

  public static TsConstructor.TsConstructorBuilder constructorBuilder() {
    return new TsConstructor.TsConstructorBuilder();
  }

  public boolean isEmpty() {
    return (interfaces.isEmpty()) && properties.isEmpty() && functions.isEmpty();
  }

  public String emit(String indent, String parentNamespace) {

    if (isEmpty()) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    if (nonNull(tsDoc)) {
      sb.append(tsDoc.emit(indent, deprecated));
    }
    sb.append(modifiers.stream().map(TsModifier::emit).collect(Collectors.joining(" ")));
    sb.append("class ");
    sb.append(emitType(parentNamespace));
    if (nonNull(superClass)) {
      sb.append(" extends " + superClass.emitType(parentNamespace));
    }

    if (!interfaces.isEmpty()) {
      sb.append(" implements ");
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

    if (nonNull(constructor) && !emitProtectedConstructor) {
      sb.append(NEW_LINE);
      sb.append(constructor.emit(indent + INDENT, namespace));
      sb.append(NEW_LINE);
    }

    if (emitProtectedConstructor) {
      sb.append(NEW_LINE);
      sb.append(
          TsConstructor.TsConstructorBuilder.protectedConstructor()
              .emit(indent + INDENT, namespace));
      sb.append(NEW_LINE);
    }

    sb.append(
        functions.stream()
            .map(function -> function.emit(indent + INDENT, namespace))
            .collect(Collectors.joining(NEW_LINE, optionalln(functions), optionalln(functions))));

    sb.append(indent).append("}").append(NEW_LINE);

    return sb.toString();
  }

  private String emitType(String parentNamespace) {
    StringBuffer sb = new StringBuffer();
    if (this.namespace.equals(parentNamespace) || namespace.isEmpty()) {
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

  public static class TsClassBuilder implements IsClassBuilder<TsClassBuilder> {
    private final TsClass tsClass;

    private TsClassBuilder(String name, String namespace) {
      this.tsClass = new TsClass(name, namespace);
      this.tsClass.builder = this;
    }

    public TsClassBuilder addModifiers(TsModifier... modifiers) {
      this.tsClass.modifiers.addAll(Arrays.asList(modifiers));
      return this;
    }

    public TsClassBuilder superClass(TsClass superClass) {
      this.tsClass.superClass = superClass;
      return this;
    }

    @Override
    public TsClassBuilder addProperty(TsProperty property) {
      this.tsClass.properties.add(property);
      return this;
    }

    @Override
    public TsClassBuilder setConstructor(TsConstructor constructor) {
      this.tsClass.constructor = constructor;
      return this;
    }

    @Override
    public TsClassBuilder addFunction(TsMethod function) {
      this.tsClass.functions.add(function);
      return this;
    }

    @Override
    public TsClassBuilder addTypeArgument(TsType typeArgument) {
      this.tsClass.typeArguments.add(typeArgument);
      return this;
    }

    @Override
    public TsClassBuilder addInterface(TsInterface tsInterface) {
      this.tsClass.interfaces.add(tsInterface);
      return this;
    }

    @Override
    public TsClassBuilder setDocs(TsDoc tsDoc) {
      this.tsClass.tsDoc = tsDoc;
      return this;
    }

    public TsClassBuilder setDeprecated(boolean deprecated) {
      this.tsClass.deprecated = deprecated;
      return this;
    }

    public TsClassBuilder setEmitProtectedContr(boolean emit) {
      this.tsClass.emitProtectedConstructor = emit;
      return this;
    }

    public TsClass build() {
      return tsClass;
    }
  }

  public static class TsConstructor {

    private List<TsModifier> modifiers = new ArrayList<>();
    private List<TsProperty> properties = new ArrayList<>();
    private TsDoc tsDoc;
    private boolean deprecated;

    private TsConstructor() {}

    public String emit(String indent, String parentNamespace) {
      StringBuffer sb = new StringBuffer();
      if (nonNull(tsDoc)) {
        sb.append(tsDoc.emit(indent, deprecated));
      }
      if (!modifiers.isEmpty()) {
        sb.append(indent);
      }
      sb.append(modifiers.stream().map(TsModifier::emit).collect(Collectors.joining(" ")));

      sb.append("constructor");
      sb.append("(");
      sb.append(
          properties.stream()
              .map(property -> property.emit(NONE, NONE, parentNamespace))
              .collect(Collectors.joining(", ")));
      sb.append(");");

      return sb.toString();
    }

    public static class TsConstructorBuilder
        implements HasProperties<TsConstructorBuilder>, HasDocs<TsConstructorBuilder> {
      private final TsConstructor constructor;

      private TsConstructorBuilder() {
        this.constructor = new TsConstructor();
      }

      public TsConstructorBuilder addModifiers(TsModifier... modifiers) {
        this.constructor.modifiers.addAll(Arrays.asList(modifiers));
        return this;
      }

      @Override
      public TsConstructorBuilder addProperty(TsProperty property) {
        this.constructor.properties.add(property);
        return this;
      }

      @Override
      public TsConstructorBuilder setDocs(TsDoc tsDoc) {
        this.constructor.tsDoc = tsDoc;
        return this;
      }

      public TsConstructorBuilder setDeprecated(boolean deprecated) {
        this.constructor.deprecated = deprecated;
        return this;
      }

      private static TsConstructor protectedConstructor() {
        return new TsConstructorBuilder().addModifiers(TsModifier.PROTECTED).build();
      }

      public TsConstructor build() {
        return this.constructor;
      }
    }
  }
}
