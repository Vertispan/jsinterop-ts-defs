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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TsNamespace {
  private final String namespace;
  private List<TsInterface> tsInterfaces = new ArrayList<>();
  private List<TsFunction> tsFunctions = new ArrayList<>();
  private List<TsClass> tsClasses = new ArrayList<>();
  private List<TsEnum> tsEnums = new ArrayList<>();

  public TsNamespace(String namespace) {
    this.namespace = namespace;
  }

  public void addClass(TsClass tsClass) {
    Optional<TsClass> first =
        this.tsClasses.stream().filter(c -> c.getName().equals(tsClass.getName())).findFirst();
    if (first.isPresent()) {
      first.get().mergeWith(tsClass);
    } else {
      this.tsClasses.add(tsClass);
    }
  }

  public void addInterface(TsInterface tsInterface) {
    this.tsInterfaces.add(tsInterface);
  }

  public void addFunction(TsFunction tsFunction) {
    this.tsFunctions.add(tsFunction);
  }

  public void addTsEnum(TsEnum tsEnum) {
    this.tsEnums.add(tsEnum);
  }

  public boolean isEmpty() {
    return tsInterfaces.isEmpty()
        && tsFunctions.isEmpty()
        && tsEnums.isEmpty()
        && (tsClasses.isEmpty() || tsClasses.stream().allMatch(TsClass::isEmpty));
  }

  public String emit(String indent, String parentNamespace) {
    if (isEmpty()) {
      return "";
    }
    StringBuffer sb = new StringBuffer(indent);
    sb.append(TsModifier.EXPORT.emit());
    sb.append(TsModifier.NAMESPACE.emit());

    if (nonNull(parentNamespace) && !parentNamespace.isEmpty()) {
      sb.append(parentNamespace);
      sb.append(".");
    }
    sb.append(namespace);
    sb.append(" {");
    sb.append(NEW_LINE);

    sb.append(
        tsFunctions.stream()
            .map(tsFunction -> tsFunction.emit(indent + INDENT, namespace))
            .collect(
                Collectors.joining(NEW_LINE, optionalln(tsFunctions), optionalln(tsFunctions))));

    sb.append(
        tsInterfaces.stream()
            .map(tsInterface -> tsInterface.emit(indent + INDENT, namespace))
            .collect(
                Collectors.joining(NEW_LINE, optionalln(tsInterfaces), optionalln(tsInterfaces))));
    sb.append(
        tsClasses.stream()
            .map(tsClass -> tsClass.emit(indent + INDENT, namespace))
            .collect(Collectors.joining(NEW_LINE, optionalln(tsClasses), optionalln(tsClasses))));

    sb.append(
        tsEnums.stream()
            .map(tsEnum -> tsEnum.emit(indent + INDENT, namespace))
            .collect(Collectors.joining(NEW_LINE, optionalln(tsEnums), optionalln(tsEnums))));

    sb.append("}").append(NEW_LINE);

    return sb.toString();
  }

  public Optional<TsClass.TsClassBuilder> findClass(String name) {

    return tsClasses.stream()
        .filter(tsClass -> tsClass.getName().equals(name))
        .map(TsClass::getBuilder)
        .findFirst();
  }

  public Optional<TsInterface.TsInterfaceBuilder> findInterface(String name) {

    return tsInterfaces.stream()
        .filter(tsInterface -> tsInterface.getName().equals(name))
        .map(TsInterface::getBuilder)
        .findFirst();
  }
}
