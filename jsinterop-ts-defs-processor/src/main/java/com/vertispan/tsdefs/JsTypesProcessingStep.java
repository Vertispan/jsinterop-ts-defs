/*
 * #%L
 * gwt-websockets-processor
 * %%
 * Copyright (C) 2011 - 2018 Vertispan LLC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.vertispan.tsdefs;

import static java.util.Objects.isNull;

import com.google.auto.common.BasicAnnotationProcessor.ProcessingStep;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.vertispan.tsdefs.annotations.TsModule;
import com.vertispan.tsdefs.model.TypeScriptModule;
import com.vertispan.tsdefs.visitors.TypeVisitor;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import jsinterop.annotations.*;

public class JsTypesProcessingStep implements ProcessingStep, HasProcessorEnv {

  private final ProcessingEnvironment processingEnv;

  public JsTypesProcessingStep(ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
  }

  @Override
  public Set<? extends Class<? extends Annotation>> annotations() {
    return new HashSet<>(
        Arrays.asList(
            JsType.class,
            JsFunction.class,
            JsConstructor.class,
            JsMethod.class,
            JsProperty.class,
            TsModule.class));
  }

  @Override
  public Set<? extends Element> process(
      SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {

    try {
      TypeScriptModule.TsModuleBuilder tsModuleBuilder = TypeScriptModule.builder(this);

      // We need to exclude native types from being exported.
      Set<Element> jsTypes =
          elementsByAnnotation.get(JsType.class).stream()
              .filter(element -> !element.getAnnotation(JsType.class).isNative())
              .collect(Collectors.toSet());

      // We also list types that might not be annotated with JsType but have members annotated using
      // JsInterop annotations
      Set<Element> jsFunctions = elementsByAnnotation.get(JsFunction.class);

      Set<Element> constructorsParentElements =
          elementsByAnnotation.get(JsConstructor.class).stream()
              .filter(element -> isNull(element.getEnclosingElement().getAnnotation(JsType.class)))
              .filter(element -> !element.getModifiers().contains(Modifier.NATIVE))
              .map(Element::getEnclosingElement)
              .collect(Collectors.toSet());

      Set<Element> methodsParentElements =
          elementsByAnnotation.get(JsMethod.class).stream()
              .filter(element -> isNull(element.getEnclosingElement().getAnnotation(JsType.class)))
              .filter(element -> !element.getModifiers().contains(Modifier.NATIVE))
              .map(Element::getEnclosingElement)
              .collect(Collectors.toSet());

      Set<Element> propertiesParentElements =
          elementsByAnnotation.get(JsProperty.class).stream()
              .filter(element -> isNull(element.getEnclosingElement().getAnnotation(JsType.class)))
              .filter(element -> !element.getModifiers().contains(Modifier.NATIVE))
              .map(Element::getEnclosingElement)
              .collect(Collectors.toSet());

      Set<Element> eligibleElements = new HashSet<>();
      eligibleElements.addAll(jsTypes);
      eligibleElements.addAll(jsFunctions);
      eligibleElements.addAll(constructorsParentElements);
      eligibleElements.addAll(methodsParentElements);
      eligibleElements.addAll(propertiesParentElements);

      eligibleElements.stream()
          .filter(
              element ->
                  isNull(element.getAnnotation(JsType.class))
                      || !element.getAnnotation(JsType.class).isNative())
          .forEach(
              e -> {
                try {
                  new TypeVisitor(e, this).visit(tsModuleBuilder);
                } catch (Exception exception) {
                  messager()
                      .printMessage(
                          Diagnostic.Kind.ERROR, Formatting.formatException(exception), e);
                  throw new RuntimeException(exception);
                }
              });

      String moduleName =
          elementsByAnnotation.get(TsModule.class).stream()
              .map(element -> element.getAnnotation(TsModule.class).value())
              .findFirst()
              .orElse("types");

      FileObject resource = null;
      resource =
          processingEnv
              .getFiler()
              .createResource(StandardLocation.CLASS_OUTPUT, "", moduleName + ".d.ts");
      try (PrintWriter writer = new PrintWriter(resource.openOutputStream())) {
        writer.print("// Minimum TypeScript Version: 4.3\n");
        writer.print(tsModuleBuilder.build().emit());
      }
    } catch (Exception e) {
      messager().printMessage(Diagnostic.Kind.ERROR, Formatting.formatException(e));
      throw new RuntimeException(e);
    }

    return Sets.newHashSet();
  }

  @Override
  public Types types() {
    return processingEnv.getTypeUtils();
  }

  @Override
  public Elements elements() {
    return processingEnv.getElementUtils();
  }

  @Override
  public Messager messager() {
    return processingEnv.getMessager();
  }

  @Override
  public ProcessingEnvironment env() {
    return processingEnv;
  }
}
