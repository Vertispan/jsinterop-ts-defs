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
package com.vertispan.tsdefs.doclet;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.TreePath;
import com.vertispan.tsdefs.annotations.TsModule;
import com.vertispan.tsdefs.impl.Formatting;
import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.LogWrapper;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsDoc;
import com.vertispan.tsdefs.impl.model.TypeScriptModule;
import com.vertispan.tsdefs.impl.visitors.TypeVisitor;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.processing.Messager;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jsinterop.annotations.*;

public class TsDoclet implements Doclet, HasProcessorEnv {
  private Locale locale;
  private Reporter reporter;
  private DocletEnvironment env;
  private Messager messager;
  private DocletLogWrapper logWrapper;

  public static final String TSOUT_DIR = "-d";

  private String outputDir;

  @Override
  public void init(Locale locale, Reporter reporter) {
    this.locale = locale;
    this.reporter = reporter;
    this.logWrapper = new DocletLogWrapper(reporter);
  }

  @Override
  public String getName() {
    return "jsiterop-ts-defs-doclet";
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_11;
  }

  @Override
  public Set<? extends Option> getSupportedOptions() {
    return Set.of(
        TsDocletOption.of(
            "-d",
            "Type script generated definition file location",
            (opt, arguments) -> {
              if (arguments.isEmpty()) {
                reporter.print(
                    Diagnostic.Kind.ERROR, "You must specify an output filepath with " + TSOUT_DIR);
                return false;
              }
              outputDir = arguments.get(0);
              return true;
            }),
        TsDocletOption.ignored("-doctitle"),
        TsDocletOption.ignored("-windowtitle"),
        TsDocletOption.ignored("-notimestamp"));
  }

  @Override
  public boolean run(DocletEnvironment environment) {
    this.env = environment;
    reporter.print(Diagnostic.Kind.NOTE, "Running doclet.");
    try {
      TypeScriptModule.TsModuleBuilder tsModuleBuilder = TypeScriptModule.builder(this);

      Set<Element> jsTypes =
          environment.getIncludedElements().stream()
              .filter(
                  element ->
                      nonNull(element.getAnnotation(JsType.class))
                          && !element.getAnnotation(JsType.class).isNative())
              .collect(Collectors.toSet());

      Set<Element> jsFunctions =
          environment.getIncludedElements().stream()
              .filter(element -> nonNull(element.getAnnotation(JsFunction.class)))
              .collect(Collectors.toSet());

      Set<Element> constructorsParentElements =
          environment.getIncludedElements().stream()
              .filter(element -> isNull(element.getAnnotation(JsType.class)))
              .filter(
                  element ->
                      element.getEnclosedElements().stream()
                          .anyMatch(
                              e ->
                                  nonNull(e.getAnnotation(JsConstructor.class))
                                      && !e.getModifiers().contains(Modifier.NATIVE)))
              .collect(Collectors.toSet());

      Set<Element> methodsParentElements =
          environment.getIncludedElements().stream()
              .filter(element -> isNull(element.getAnnotation(JsType.class)))
              .filter(
                  element ->
                      element.getEnclosedElements().stream()
                          .anyMatch(
                              e ->
                                  nonNull(e.getAnnotation(JsMethod.class))
                                      && !e.getModifiers().contains(Modifier.NATIVE)))
              .collect(Collectors.toSet());

      Set<Element> propertiesParentElements =
          environment.getIncludedElements().stream()
              .filter(element -> isNull(element.getAnnotation(JsType.class)))
              .filter(
                  element ->
                      element.getEnclosedElements().stream()
                          .anyMatch(
                              e ->
                                  nonNull(e.getAnnotation(JsProperty.class))
                                      && !e.getModifiers().contains(Modifier.NATIVE)))
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
          environment.getIncludedElements().stream()
              .filter(element -> nonNull(element.getAnnotation(TsModule.class)))
              .findFirst()
              .map(element -> element.getAnnotation(TsModule.class).value())
              .orElse("types");

      File outFile = Paths.get(outputDir, moduleName + ".d.ts").toFile();
      try {
        Files.createDirectories(outFile.getParentFile().toPath());
      } catch (IOException e) {
        messager().printMessage(Diagnostic.Kind.ERROR, Formatting.formatException(e));
        throw new RuntimeException(e);
      }

      try (PrintWriter writer = new PrintWriter(outFile)) {
        writer.print("// Minimum TypeScript Version: 4.3\n");
        writer.print("// Generated using " + this.getClass().getCanonicalName() + "\n");
        writer.print(tsModuleBuilder.build().emit());
      }
    } catch (Exception e) {
      messager().printMessage(Diagnostic.Kind.ERROR, Formatting.formatException(e));
      throw new RuntimeException(e);
    }

    return true;
  }

  @Override
  public TsDoc getDocs(Element element) {

    TreePath path = env.getDocTrees().getPath(element);
    if (nonNull(path)) {
      DocCommentTree tree = env.getDocTrees().getDocCommentTree(path);
      if (nonNull(tree)) {
        Optional<DocCommentTree> docCommentTree =
            Optional.ofNullable(path).map(treePath -> env.getDocTrees().getDocCommentTree(path));
        return docCommentTree
            .map(doctree -> TsDoc.of(doctree.accept(new TsDocTreeVisitor(this, this.env), element)))
            .orElse(inheritedDocsOrEmpty(element));
      }
    }
    return inheritedDocsOrEmpty(element);
  }

  private TsDoc inheritedDocsOrEmpty(Element element) {

    TsElement tsElement = TsElement.of(element, this);
    if (tsElement.overridesAnyMethod()) {
      return TsDoc.inherited();
    }
    return TsDoc.empty();
  }

  @Override
  public Types types() {
    return env.getTypeUtils();
  }

  @Override
  public Elements elements() {
    return env.getElementUtils();
  }

  @Override
  public LogWrapper messager() {
    return this.logWrapper;
  }
}
