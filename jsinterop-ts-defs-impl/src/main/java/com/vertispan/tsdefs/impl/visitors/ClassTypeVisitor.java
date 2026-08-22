/*
 * Copyright © 2024 Vertispan
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

import static java.util.stream.Collectors.groupingBy;

import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsClass;
import com.vertispan.tsdefs.impl.model.TsModifier;
import com.vertispan.tsdefs.impl.model.TypeScriptModule;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class ClassTypeVisitor extends TsElement {

  public ClassTypeVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(TypeScriptModule.TsModuleBuilder moduleBuilder) {
    if (isClass() && isPublic() && !isTsTypeDef() && !isTsIgnored() && !isEnum()) {

      TsClass.TsClassBuilder builder =
          TsClass.builder(getName(), getNamespace())
              .setDocs(getDocs())
              .setDeprecated(isDeprecated())
              .addModifiers(TsModifier.EXPORT)
              .addModifiers(getJsModifiers());
      new TypeArgumentsVisitor<TsClass.TsClassBuilder>(element.asType(), env).visit(builder);
      getSuperClass(element).ifPresent(builder::superClass);
      new InterfacesVisitor<TsClass.TsClassBuilder>(element, env).visit(builder);

      element.getEnclosedElements().stream()
          .map(e -> TsElement.of(e, env))
          .filter(
              e ->
                  !e.getDeclaredNamespace().isPresent()
                      || e.getNamespace().equals(getNamespace())
                      || (e.getDeclaredNamespace().isPresent() && isSameNameSpaceAsParent(e)))
          .forEach(e -> visit(builder, e.element()));

      new InheritedMethodsVisitor<TsClass.TsClassBuilder>(element, env).visit(builder);
      new SetterGetterMethodsVisitor<TsClass.TsClassBuilder>(element, env).visit(builder);

      getJavaSuperClass()
          .ifPresent(
              superclass -> {
                new SuperTsInterfaceVisitor<TsClass.TsClassBuilder>(superclass, env).visit(builder);
              });

      TsClass tsClass = builder.build();
      getJavaSuperClass().ifPresent(superclass -> processSuperClass(tsClass, superclass));
      builder.setEmitProtectedContr(requiresProtectedConstructor());

      moduleBuilder.addClass(tsClass);

      // Has own namespace
      Map<String, List<TsElement>> withDiffNs =
          element.getEnclosedElements().stream()
              .map(e -> TsElement.of(e, env))
              .filter(tsElement -> tsElement.getDeclaredNamespace().isPresent())
              .filter(e -> e.isExportable() && !isSameNameSpaceAsParent(e))
              .collect(groupingBy(TsElement::getNamespace));

      // Handle the case when a member has a different name space than its enclosing element
      // So we create a type on the fly on that namespace.
      buildTypesFromNamespaces(moduleBuilder, withDiffNs);
    }
  }

  private void processSuperClass(TsClass tsClass, TypeMirror superclass) {
    TsElement superTsElement = TsElement.of(superclass, env);

    if (superTsElement.isTsIgnored() || superTsElement.isTsInterface()) {
      mergeSuperClass(tsClass, superTsElement);
    }

    superTsElement
        .getJavaSuperClass()
        .ifPresent(
            typeMirror -> {
              processSuperClass(tsClass, typeMirror);
            });
  }

  private void mergeSuperClass(TsClass tsClass, TsElement superTsElement) {
    TsClass.TsClassBuilder superBuilder =
        TsClass.builder(superTsElement.getName(), superTsElement.getNamespace());
    superTsElement.element().getEnclosedElements().forEach(e -> visit(superBuilder, e));
    TsClass superTsClass = superBuilder.build();
    tsClass.mergeFunctions(superTsClass);
    tsClass.mergeProperties(superTsClass);
  }

  private boolean isSameNameSpaceAsParent(TsElement e) {
    if (e.getDeclaredNamespace().isPresent()) {
      String childNameSpace = e.getDeclaredNamespace().get();
      return childNameSpace.equals(getNamespace())
          || childNameSpace.equals(e.getNamespace() + "." + e.getName());
    }
    return true;
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

  private Optional<TsClass> getSuperClass(Element element) {
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

      TsClass.TsClassBuilder builder =
          TsClass.builder(superTsElement.getName(), superTsElement.getNamespace());
      new TypeArgumentsVisitor<TsClass.TsClassBuilder>(superclass, env).visit(builder);
      return Optional.of(builder.build());
    } else {
      return getSuperClass(superTsElement.element());
    }
  }
}
