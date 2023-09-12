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

import static com.vertispan.tsdefs.impl.Formatting.*;

import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.HasFunctions;
import com.vertispan.tsdefs.impl.builders.HasMembers;
import com.vertispan.tsdefs.impl.builders.HasProperties;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.visitors.FixedNsClassMethodVisitor;
import com.vertispan.tsdefs.impl.visitors.FixedNsFieldVisitor;
import java.util.*;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;

public class TypeScriptModule {
  private Map<String, TsNamespace> namespaces = new HashMap<>();
  private List<TsInterface> globalInterfaces = new ArrayList<>();
  private List<TsFunction> globalFunctions = new ArrayList<>();
  private List<TsClass> globalClasses = new ArrayList<>();
  private List<TsEnum> globalEnums = new ArrayList<>();

  private final HasProcessorEnv env;

  public TypeScriptModule(HasProcessorEnv env) {
    this.env = env;
  }

  public String emit() {
    StringBuffer sb = new StringBuffer();

    sb.append(
        globalInterfaces.stream()
            .map(tsInterface -> tsInterface.emit(NONE, NONE))
            .collect(
                Collectors.joining(
                    NEW_LINE, optionalln(globalInterfaces), optionalln(globalInterfaces))));

    sb.append(
        globalFunctions.stream()
            .map(tsFunction -> tsFunction.emit(NONE, NONE))
            .collect(
                Collectors.joining(
                    NEW_LINE, optionalln(globalFunctions), optionalln(globalFunctions))));

    sb.append(
        globalEnums.stream()
            .map(tsEnum -> tsEnum.emit(NONE, NONE))
            .collect(
                Collectors.joining(NEW_LINE, optionalln(globalEnums), optionalln(globalEnums))));

    sb.append(
        globalClasses.stream()
            .map(tsClass -> tsClass.emit(NONE, NONE))
            .collect(
                Collectors.joining(
                    NEW_LINE, optionalln(globalClasses), optionalln(globalClasses))));

    sb.append(
        namespaces.values().stream()
            .map(namespace -> namespace.emit(NONE, NONE))
            .collect(
                Collectors.joining(
                    NEW_LINE, optionalln(globalClasses), optionalln(globalClasses))));

    return sb.toString();
  }

  public static TsModuleBuilder builder(HasProcessorEnv env) {
    return new TsModuleBuilder(env);
  }

  public static class TsModuleBuilder {

    private final TypeScriptModule typeScriptModule;

    private TsModuleBuilder(HasProcessorEnv env) {
      this.typeScriptModule = new TypeScriptModule(env);
    }

    public TsModuleBuilder addInterface(TsInterface tsInterface) {
      if (tsInterface.isGlobal()) {
        typeScriptModule.globalInterfaces.add(tsInterface);
      } else {
        if (!typeScriptModule.namespaces.containsKey(tsInterface.getNamespace())) {
          typeScriptModule.namespaces.put(
              tsInterface.getNamespace(), new TsNamespace(tsInterface.getNamespace()));
        }
        typeScriptModule.namespaces.get(tsInterface.getNamespace()).addInterface(tsInterface);
      }
      return this;
    }

    public TsModuleBuilder addFunction(TsFunction tsFunction) {
      if (tsFunction.isGlobal()) {
        typeScriptModule.globalFunctions.add(tsFunction);
      } else {
        if (!typeScriptModule.namespaces.containsKey(tsFunction.getNamespace())) {
          typeScriptModule.namespaces.put(
              tsFunction.getNamespace(), new TsNamespace(tsFunction.getNamespace()));
        }
        typeScriptModule.namespaces.get(tsFunction.getNamespace()).addFunction(tsFunction);
      }
      return this;
    }

    public TsModuleBuilder addClass(TsClass tsClass) {
      if (tsClass.isGlobal()) {
        typeScriptModule.globalClasses.add(tsClass);
      } else {
        if (!typeScriptModule.namespaces.containsKey(tsClass.getNamespace())) {
          typeScriptModule.namespaces.put(
              tsClass.getNamespace(), new TsNamespace(tsClass.getNamespace()));
        }
        typeScriptModule.namespaces.get(tsClass.getNamespace()).addClass(tsClass);
      }
      return this;
    }

    public TsModuleBuilder addTsEnum(TsEnum tsEnum) {
      if (tsEnum.isGlobal()) {
        typeScriptModule.globalEnums.add(tsEnum);
      } else {
        if (!typeScriptModule.namespaces.containsKey(tsEnum.getNamespace())) {
          typeScriptModule.namespaces.put(
              tsEnum.getNamespace(), new TsNamespace(tsEnum.getNamespace()));
        }
        typeScriptModule.namespaces.get(tsEnum.getNamespace()).addTsEnum(tsEnum);
      }
      return this;
    }

    public Optional<TsClass.TsClassBuilder> findClass(String namespace, String name) {
      if (!typeScriptModule.namespaces.containsKey(namespace)) {
        return Optional.empty();
      }

      return typeScriptModule.namespaces.get(namespace).findClass(name);
    }

    public Optional<TsInterface.TsInterfaceBuilder> findInterface(String namespace, String name) {
      if (!typeScriptModule.namespaces.containsKey(namespace)) {
        return Optional.empty();
      }

      return typeScriptModule.namespaces.get(namespace).findInterface(name);
    }

    public TsClass.TsClassBuilder createTypeBuilder(String namespace, String name) {
      TsClass.TsClassBuilder typeBuilder =
          TsClass.builder(name, namespace).addModifiers(TsModifier.EXPORT).setDocs(TsDoc.empty());
      return typeBuilder;
    }

    public TypeScriptModule build() {
      return this.typeScriptModule;
    }

    public void addToNewType(String namespace, String name, List<TsElement> tsElements) {
      TsClass.TsClassBuilder typeBuilder = createTypeBuilder(namespace, name);
      tsElements.forEach(tsElement -> visitFixedNs(namespace, typeBuilder, tsElement.element()));
      addClass(typeBuilder.build());
    }

    private void visitFixedNs(String namespace, HasMembers<?> builder, Element enclosedElement) {
      new FixedNsFieldVisitor<HasMembers<?>>(namespace, enclosedElement, typeScriptModule.env)
          .visit((HasProperties<HasMembers<?>>) builder);
      new FixedNsClassMethodVisitor<HasMembers<?>>(namespace, enclosedElement, typeScriptModule.env)
          .visit((HasFunctions<HasMembers<?>>) builder);
    }
  }
}
