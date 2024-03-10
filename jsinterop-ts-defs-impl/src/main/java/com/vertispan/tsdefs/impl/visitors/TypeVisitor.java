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

import static java.util.Objects.nonNull;

import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TypeScriptModule;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import jsinterop.annotations.JsConstructor;

public class TypeVisitor extends TsElement {

  public static final String MULTIPLE_CONSTRUCTORS_ARE_NOT_ALLOWED =
      "Multiple constructors are not allowed in JS exports";
  public static final String DUPLICATE_MEMBERS_ARE_NOT_ALLOWED =
      "Members with duplicate names are not allowed in JS exports.";
  public static final String PUBLIC_FIELD_WITH_PUBLIC_ACCESSOR_NOT_ALLOWED_IN_JS_EXPORTS =
      "Public field with public accessors not allowed in in JS exports";

  public TypeVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(TypeScriptModule.TsModuleBuilder module) {
    validate();
    new ClassTypeVisitor(element, env).visit(module);
    new InterfaceTypeVisitor(element, env).visit(module);
    new TsTypeDefVisitor(element, env).visit(module);
    new TsEnumVisitor(element, env).visit(module);
  }

  private void validate() {
    singleConstructorValidation();
    duplicateMembersValidation();
    publicMemberWithPublicAccessor();
  }

  private void publicMemberWithPublicAccessor() {
    element.getEnclosedElements().stream()
        .map(e -> TsElement.of(e, env))
        .filter(e -> e.isField() && e.isPublic())
        .filter(this::hasPublicAccessor)
        .forEach(
            e ->
                env.messager()
                    .printMessage(
                        Diagnostic.Kind.ERROR,
                        PUBLIC_FIELD_WITH_PUBLIC_ACCESSOR_NOT_ALLOWED_IN_JS_EXPORTS,
                        e.element()));
  }

  private boolean hasPublicAccessor(TsElement field) {
    return element.getEnclosedElements().stream()
        .map(e -> TsElement.of(e, env))
        .filter(e -> e.isMethod() && e.isPublic())
        .anyMatch(
            e ->
                e.getName().equals(field.getGetterName())
                    || e.getName().equals(field.getSetterName()));
  }

  private void singleConstructorValidation() {
    List<TsElement> constructors =
        element.getEnclosedElements().stream()
            .map(e -> TsElement.of(e, env))
            .filter(e -> e.isExportable() && e.isConstructor() && !e.isIgnored())
            .collect(Collectors.toList());

    if (constructors.size() > 1) {
      long count =
          constructors.stream()
              .filter(ctor -> nonNull(ctor.element().getAnnotation(JsConstructor.class)))
              .count();

      if (count != 1) {
        env.messager()
            .printMessage(Diagnostic.Kind.ERROR, MULTIPLE_CONSTRUCTORS_ARE_NOT_ALLOWED, element);
      }
    }
  }

  private void duplicateMembersValidation() {
    Set<String> nonStaticNames = new HashSet<>();
    element.getEnclosedElements().stream()
        .map(e -> TsElement.of(e, env))
        .filter(tsElement -> tsElement.isMethod() || tsElement.isField())
        .filter(e -> !e.isConstructor())
        .filter(
            e ->
                e.isExportable()
                    && !e.isSetter()
                    && !e.isGetter()
                    && !e.isIgnored()
                    && !e.isStatic())
        .filter(e -> !nonStaticNames.add(e.getName()))
        .collect(Collectors.toList())
        .forEach(
            duplicate ->
                env.messager()
                    .printMessage(
                        Diagnostic.Kind.ERROR,
                        DUPLICATE_MEMBERS_ARE_NOT_ALLOWED,
                        duplicate.element()));

    Set<String> staticNames = new HashSet<>();
    element.getEnclosedElements().stream()
        .map(e -> TsElement.of(e, env))
        .filter(tsElement -> tsElement.isMethod() || tsElement.isField())
        .filter(e -> !e.isConstructor())
        .filter(
            e ->
                e.isExportable()
                    && !e.isSetter()
                    && !e.isGetter()
                    && !e.isIgnored()
                    && e.isStatic())
        .filter(e -> !staticNames.add(e.getName()))
        .collect(Collectors.toList())
        .forEach(
            duplicate ->
                env.messager()
                    .printMessage(
                        Diagnostic.Kind.ERROR,
                        DUPLICATE_MEMBERS_ARE_NOT_ALLOWED,
                        duplicate.element()));

    Set<String> setGetNames = new HashSet<>();
    element.getEnclosedElements().stream()
        .map(e -> TsElement.of(e, env))
        .filter(tsElement -> tsElement.isMethod() || tsElement.isField())
        .filter(e -> !e.isConstructor())
        .filter(e -> e.isExportable() && (e.isSetter() || e.isGetter()))
        .map(TsElement::nonGetSetName)
        .forEach(setGetNames::add);

    element.getEnclosedElements().stream()
        .map(e -> TsElement.of(e, env))
        .filter(TsElement::isField)
        .filter(TsElement::isExportable)
        .filter(e -> !setGetNames.add(e.getName()))
        .collect(Collectors.toList())
        .forEach(
            duplicate ->
                env.messager()
                    .printMessage(
                        Diagnostic.Kind.ERROR,
                        DUPLICATE_MEMBERS_ARE_NOT_ALLOWED,
                        duplicate.element()));
  }
}
