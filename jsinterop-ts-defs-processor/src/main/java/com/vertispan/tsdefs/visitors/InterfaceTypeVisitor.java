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
import com.vertispan.tsdefs.builders.TsElement;
import com.vertispan.tsdefs.model.TsInterface;
import com.vertispan.tsdefs.model.TsModifier;
import com.vertispan.tsdefs.model.TypeScriptModule;
import javax.lang.model.element.Element;
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
                }
              });

      new TypeArgumentsVisitor<TsInterface.TsInterfaceBuilder>(element.asType(), env)
          .visit(builder);
      new InterfacesVisitor<TsInterface.TsInterfaceBuilder>(element, env).visit(builder);

      allSuperInterfacesMethods(typeMirror -> TsElement.of(typeMirror, env).isTsIgnored())
          .forEach(
              method ->
                  new ClassMethodVisitor<TsInterface.TsInterfaceBuilder>(method, env)
                      .visit(builder));

      element.getEnclosedElements().forEach(e -> visit(builder, e));
      new InheritedMethodsVisitor<TsInterface.TsInterfaceBuilder>(element, env).visit(builder);

      moduleBuilder.addInterface(builder.build());
    }
  }

  private void visit(TsInterface.TsInterfaceBuilder interfaceBuilder, Element enclosedElement) {
    new ClassMethodVisitor<TsInterface.TsInterfaceBuilder>(enclosedElement, env)
        .visit(interfaceBuilder);
  }
}
