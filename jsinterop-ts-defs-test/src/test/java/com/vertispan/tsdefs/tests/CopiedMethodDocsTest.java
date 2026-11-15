/*
 * Copyright © 2026 Vertispan
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
package com.vertispan.tsdefs.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.vertispan.tsdefs.processor.JsTypesProcessor;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.StandardLocation;
import org.junit.Test;

/** A method copied into a subtype keeps its docs, as a copied accessor does. */
public class CopiedMethodDocsTest {

  private static String definitions() throws IOException {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                Stream.of(
                        "DocsSource",
                        "DocsView",
                        "DocsJsView",
                        "DocsAbstract",
                        "DocsShown",
                        "DocsShownImpl")
                    .map(name -> JavaFileObjects.forResource("copieddocs/" + name + ".java"))
                    .collect(Collectors.toList()));
    assertThat(result.errors()).isEmpty();
    return result
        .generatedFile(StandardLocation.CLASS_OUTPUT, "", "types.d.ts")
        .orElseThrow(AssertionError::new)
        .getCharContent(false)
        .toString();
  }

  /** The declaration of {@code kindAndName}, up to its closing brace. */
  private static String declaration(String definitions, String kindAndName) {
    Matcher matcher =
        Pattern.compile("export " + kindAndName + "\\b.*?\n\t}", Pattern.DOTALL)
            .matcher(definitions);
    assertThat(matcher.find()).as("declaration of %s", kindAndName).isTrue();
    return matcher.group();
  }

  @Test
  public void methodCopiedIntoATsInterfaceKeepsItsDocs() throws IOException {
    assertThat(declaration(definitions(), "interface DocsView"))
        .containsPattern("Doc for close\\.\\s*\\*/\\s*close\\(\\):void;");
  }

  @Test
  public void methodCopiedIntoAJsTypeInterfaceKeepsItsDocs() throws IOException {
    assertThat(declaration(definitions(), "interface DocsJsView"))
        .containsPattern("Doc for close\\.\\s*\\*/\\s*close\\(\\):void;");
  }

  @Test
  public void methodCopiedIntoAClassKeepsItsDocs() throws IOException {
    assertThat(declaration(definitions(), "abstract class DocsAbstract"))
        .containsPattern("Doc for close\\.\\s*\\*/\\s*close\\(\\):void;");
  }

  /** The supertype's own declaration carries the doc in TypeScript (#78). */
  @Test
  public void methodCopiedFromAnEmittedSupertypeHasNoDocs() throws IOException {
    assertThat(declaration(definitions(), "class DocsShownImpl"))
        .contains("open():void;")
        .doesNotContain("Doc for open");
  }
}
