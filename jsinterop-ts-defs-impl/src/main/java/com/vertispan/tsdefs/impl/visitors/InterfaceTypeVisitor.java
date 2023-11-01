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
package com.vertispan.tsdefs.impl.visitors;

import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsInterface;
import com.vertispan.tsdefs.impl.model.TsMethod;
import com.vertispan.tsdefs.impl.model.TsModifier;
import com.vertispan.tsdefs.impl.model.TsProperty;
import com.vertispan.tsdefs.impl.model.TypeScriptModule;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

public class InterfaceTypeVisitor extends TsElement {
  public static final String TS_INTERFACE_CANNOT_EXTEND_FROM_NONE_TS_INTERFACE_CLASSES =
      "TsInterface classes cannot extend from non TsInterface classes.";

  public InterfaceTypeVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(TypeScriptModule.TsModuleBuilder moduleBuilder) {
    if (isInterface() && isPublic() && !isJsFunction() & !isTsIgnored()) {
      TsInterface.TsInterfaceBuilder builder =
          TsInterface.builder(getName(), getNamespace())
              .setDocs(getDocs())
              .setDeprecated(isDeprecated())
              .addModifiers(TsModifier.EXPORT);

      getJavaSuperClass()
          .ifPresent(
              superclass -> {
                if (!TsElement.of(superclass, env).isTsInterface()) {
                  env.messager()
                      .printMessage(
                          Diagnostic.Kind.ERROR,
                          TS_INTERFACE_CANNOT_EXTEND_FROM_NONE_TS_INTERFACE_CLASSES,
                          element);
                } else if (!TsElement.of(superclass, env).isTsIgnored()) {
                  addInterface(builder, superclass);
                }
              });

      new TypeArgumentsVisitor<TsInterface.TsInterfaceBuilder>(element.asType(), env)
          .visit(builder);
      new InterfacesVisitor<TsInterface.TsInterfaceBuilder>(element, env).visit(builder);

      element.getEnclosedElements().forEach(e -> visit(builder, e));
      if (isTsInterface()) {
        element.getEnclosedElements().stream()
            .filter(member -> ElementKind.FIELD == member.getKind())
            .map(field -> TsElement.of(field, env))
            .filter(TsElement::isExportable)
            .forEach(
                tsElement -> {
                  if (tsElement.isFinal()) {
                    builder.addFunction(
                        TsMethod.builder(tsElement.getName(), tsElement.getType())
                            .addModifiers(TsModifier.GET)
                            .setDocs(tsElement.getDocs())
                            .setDeprecated(tsElement.isDeprecated())
                            .build());
                  } else {
                    builder.addProperty(
                        TsProperty.builder(tsElement.getName(), tsElement.getType())
                            .setDocs(tsElement.getDocs())
                            .setDeprecated(tsElement.isDeprecated())
                            .setOptional(tsElement.isJsNullable())
                            .build());
                  }
                });
      }
      if (!isTsInterface()) {
        new InheritedMethodsVisitor<TsInterface.TsInterfaceBuilder>(element, env).visit(builder);
      }
      new SetterGetterMethodsVisitor<TsInterface.TsInterfaceBuilder>(element, env).visit(builder);

      moduleBuilder.addInterface(builder.build());
    }
  }

  private void visit(TsInterface.TsInterfaceBuilder interfaceBuilder, Element enclosedElement) {
    new ClassMethodVisitor<TsInterface.TsInterfaceBuilder>(enclosedElement, env)
        .visit(interfaceBuilder);
  }

  private void addInterface(TsInterface.TsInterfaceBuilder builder, TypeMirror interfaceElement) {
    TsElement tsElement = TsElement.of(interfaceElement, env);
    if (!tsElement.isTsIgnored()) {
      TsInterface.TsInterfaceBuilder interfaceBuilder =
          TsInterface.builder(tsElement.getName(), tsElement.getNamespace());
      new TypeArgumentsVisitor<TsInterface.TsInterfaceBuilder>(interfaceElement, env)
          .visit(interfaceBuilder);
      builder.addInterface(interfaceBuilder.build());
    }
  }
}
