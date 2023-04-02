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
package com.vertispan.tsdefs.visitors;

import com.vertispan.tsdefs.HasProcessorEnv;
import com.vertispan.tsdefs.builders.HasMembers;
import com.vertispan.tsdefs.builders.TsElement;
import com.vertispan.tsdefs.model.NoneTsType;
import com.vertispan.tsdefs.model.TsMethod;
import com.vertispan.tsdefs.model.TsModifier;
import com.vertispan.tsdefs.model.TsParameter;
import com.vertispan.tsdefs.model.TsProperty;
import com.vertispan.tsdefs.model.TsType;
import com.vertispan.tsdefs.model.TsVariable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class SetterGetterMethodsVisitor<T> extends TsElement {

  public SetterGetterMethodsVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(HasMembers<T> parent) {

    List<ExecutableElement> nonOverriddenMethods = allSuperMethods();

    List<ExecutableElement> enclosedMethods =
        element.getEnclosedElements().stream()
            .filter(enclosedElement -> ElementKind.METHOD == enclosedElement.getKind())
            .map(method -> (ExecutableElement) method)
            .collect(Collectors.toList());

    Map<String, TsElement> getters = new HashMap<>();

    nonOverriddenMethods.stream()
        .map(e -> TsElement.of(e, env))
        .filter(TsElement::isGetter)
        .filter(TsElement::isExportable)
        .forEach(tsElement -> getters.put(tsElement.nonGetSetName(), tsElement));

    enclosedMethods.stream()
        .map(e -> TsElement.of(e, env))
        .filter(TsElement::isGetter)
        .filter(TsElement::isExportable)
        .forEach(tsElement -> getters.put(tsElement.nonGetSetName(), tsElement));

    Map<String, TsElement> setters = new HashMap<>();

    nonOverriddenMethods.stream()
        .map(e -> TsElement.of(e, env))
        .filter(TsElement::isSetter)
        .filter(TsElement::isExportable)
        .forEach(tsElement -> setters.put(tsElement.nonGetSetName(), tsElement));

    enclosedMethods.stream()
        .map(e -> TsElement.of(e, env))
        .filter(TsElement::isSetter)
        .filter(TsElement::isExportable)
        .forEach(tsElement -> setters.put(tsElement.nonGetSetName(), tsElement));

    Set<String> allNames = new HashSet<>();
    allNames.addAll(getters.keySet());
    allNames.addAll(setters.keySet());

    Set<String> common =
        allNames.stream()
            .filter(name -> getters.containsKey(name) && setters.containsKey(name))
            .collect(Collectors.toSet());

    common.stream()
        .map(getters::get)
        .forEach(
            getter ->
                parent.addProperty(
                    TsProperty.builder(getter.nonGetSetName(), getter.getType())
                        .setDocs(getter.getDocs())
                        .setDeprecated(isDeprecated())
                        .setOptional(getter.isJsNullable())
                        .build()));

    Set<String> getterOnlyKeys = new HashSet<>(getters.keySet());
    getterOnlyKeys.removeAll(common);
    getterOnlyKeys.stream()
        .map(getters::get)
        .forEach(
            getter -> {
              if (getter.isJsNullable()) {
                TsVariable.TsPropertyBuilder<TsProperty> propertyBuilder =
                    TsProperty.builder(getter.nonGetSetName(), getter.getType())
                        .setDocs(getter.getDocs())
                        .setDeprecated(isDeprecated())
                        .setOptional(getter.isJsNullable());
                if (getter.isJsNullable()) {
                  propertyBuilder.addModifiers(TsModifier.READONLY);
                }
                parent.addProperty(propertyBuilder.build());
              } else {
                parent.addFunction(
                    TsMethod.builder(getter.nonGetSetName(), getter.getType())
                        .addModifiers(TsModifier.GET)
                        .setDocs(getter.getDocs())
                        .setDeprecated(isDeprecated())
                        .build());
              }
            });

    Set<String> setterOnlyKeys = new HashSet<>(setters.keySet());
    setterOnlyKeys.removeAll(common);
    setterOnlyKeys.stream()
        .map(setters::get)
        .forEach(
            setter ->
                parent.addFunction(
                    TsMethod.builder(setter.nonGetSetName(), new NoneTsType())
                        .addModifiers(TsModifier.SET)
                        .setDocs(setter.getDocs())
                        .setDeprecated(isDeprecated())
                        .addParameter(
                            TsParameter.builder(
                                    setter.nonGetSetName(), getTypeFromSetterArg(setter))
                                .build())
                        .build()));
  }

  private TsType getTypeFromSetterArg(TsElement setter) {
    ExecutableElement execElement = (ExecutableElement) setter.element();
    VariableElement variableElement = execElement.getParameters().get(0);
    return TsElement.of(variableElement, env).getType();
  }
}
