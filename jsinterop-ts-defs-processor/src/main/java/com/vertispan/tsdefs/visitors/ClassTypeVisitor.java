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

import static java.util.stream.Collectors.groupingBy;

import com.vertispan.tsdefs.HasProcessorEnv;
import com.vertispan.tsdefs.builders.TsElement;
import com.vertispan.tsdefs.model.TsClass;
import com.vertispan.tsdefs.model.TsModifier;
import com.vertispan.tsdefs.model.TypeScriptModule;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

public class ClassTypeVisitor extends TsElement {

  public ClassTypeVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(TypeScriptModule.TsModuleBuilder moduleBuilder) {
    if (isClass() && isPublic() && !isTsTypeDef() && !isTsIgnored()) {

      TsClass.TsClassBuilder builder =
          TsClass.builder(getName(), getNamespace())
              .setDocs(getDocs())
              .setDeprecated(isDeprecated())
              .addModifiers(TsModifier.EXPORT);
      new TypeArgumentsVisitor<TsClass.TsClassBuilder>(element.asType(), env).visit(builder);
      getSuperClass().ifPresent(builder::superClass);
      new InterfacesVisitor<TsClass.TsClassBuilder>(element, env).visit(builder);

      element.getEnclosedElements().stream()
          .map(e -> TsElement.of(e, env))
          .filter(
              e ->
                  !e.getDeclaredNamespace().isPresent()
                      || e.getNamespace().equals(getNamespace())
                      || (e.getDeclaredNamespace().isPresent()
                          && namespaceFromDeclaredNamespace(e.getDeclaredNamespace().get())
                              .equals(getNamespace())))
          .forEach(e -> visit(builder, e.element()));

      new InheritedMethodsVisitor<TsClass.TsClassBuilder>(element, env).visit(builder);

      // a ts class must export all super interfaces methods we are adding this to handle the
      // TsInterface case were a class might not override a super class annotated as TsInterface
      // method.
      allSuperInterfacesMethods().stream()
          .filter(method -> !override(method))
          .forEach(
              method -> new ClassMethodVisitor<TsClass.TsClassBuilder>(method, env).visit(builder));

      getJavaSuperClass()
          .ifPresent(
              superclass ->
                  new SuperTsInterfaceVisitor<TsClass.TsClassBuilder>(superclass, env)
                      .visit(builder));

      getInterfaces().stream()
          .map(interfaceType -> env.types().asElement(interfaceType))
          .forEach(e -> new InheritedMethodsVisitor<TsClass.TsClassBuilder>(e, env).visit(builder));

      moduleBuilder.addClass(builder.build());

      // Has own namespace
      Map<String, List<TsElement>> withDiffNs =
          element.getEnclosedElements().stream()
              .map(e -> TsElement.of(e, env))
              .filter(tsElement -> tsElement.getDeclaredNamespace().isPresent())
              .filter(
                  e ->
                      e.isExportable()
                          && !namespaceFromDeclaredNamespace(e.getDeclaredNamespace().get())
                              .equals(getNamespace()))
              .collect(groupingBy(TsElement::getNamespace));

      // Handle the case when a member has a different name space than its enclosing element
      // So we create a type on the fly on that namespace.
      buildTypesFromNamespaces(moduleBuilder, withDiffNs);

      builder.setEmitPrivateContr(requiresPrivateConstructor());
    }
  }

  private void buildTypesFromNamespaces(
      TypeScriptModule.TsModuleBuilder moduleBuilder, Map<String, List<TsElement>> withDiffNs) {
    withDiffNs
        .keySet()
        .forEach(
            declaredNamespace -> {
              String typeName = typeFromDeclaredNamespace(declaredNamespace);
              String actualNamespace = namespaceFromDeclaredNamespace(declaredNamespace);

              moduleBuilder.addToNewType(
                  actualNamespace, typeName, withDiffNs.get(declaredNamespace));
            });
  }

  private String namespaceFromDeclaredNamespace(String declaredNamespace) {
    if (declaredNamespace.contains(".")) {
      return declaredNamespace.substring(0, declaredNamespace.lastIndexOf("."));
    }
    return getNamespace();
  }

  private String typeFromDeclaredNamespace(String namespace) {
    if (namespace.contains(".")) {
      return namespace.substring(namespace.lastIndexOf(".") + 1);
    }
    return namespace;
  }

  private void visit(TsClass.TsClassBuilder classBuilder, Element enclosedElement) {
    new ConstructorVisitor<TsClass.TsClassBuilder>(enclosedElement, env).visit(classBuilder);
    new FieldVisitor<TsClass.TsClassBuilder>(enclosedElement, env).visit(classBuilder);
    new ClassMethodVisitor<TsClass.TsClassBuilder>(enclosedElement, env).visit(classBuilder);
  }

  private Optional<TsClass> getSuperClass() {
    TypeMirror superclass = ((TypeElement) element).getSuperclass();

    if (((TypeElement) env.types().asElement(superclass))
        .getSuperclass()
        .getKind()
        .equals(TypeKind.NONE)) {
      return Optional.empty();
    }
    Element superElement = env.types().asElement(superclass);
    TsElement superTsElement = TsElement.of(superElement, env);

    if (superTsElement.isExportable()
        && !superTsElement.isTsInterface()
        && !superTsElement.isTsIgnored()) {

      if (superTsElement.requiresPrivateConstructor()) {
        env.messager()
            .printMessage(
                Diagnostic.Kind.ERROR, "Cannot extend a type with private constructor.", element);
        return Optional.empty();
      }
      TsClass.TsClassBuilder builder =
          TsClass.builder(superTsElement.getName(), superTsElement.getNamespace());
      new TypeArgumentsVisitor<TsClass.TsClassBuilder>(superclass, env).visit(builder);
      return Optional.of(builder.build());
    }
    return Optional.empty();
  }
}
