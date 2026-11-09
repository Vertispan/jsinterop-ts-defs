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
import javax.tools.JavaFileObject;
import org.junit.Test;

/** Docs and deprecation decide whether a @TsInterface override is emitted. */
public class OverrideDocsTest {

  private static String definitions() throws IOException {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                Stream.of(
                        "DocBase", "DocumentedOverride", "DeprecatedOverride", "RestatesDeprecated")
                    .map(name -> JavaFileObjects.forResource("overridedocs/" + name + ".java"))
                    .collect(Collectors.toList()));
    assertThat(result.errors()).isEmpty();
    for (JavaFileObject file : result.generatedFiles()) {
      if (file.getName().endsWith(".d.ts")) {
        return file.getCharContent(true).toString();
      }
    }
    throw new AssertionError("no .d.ts generated");
  }

  /** The interface's declaration, with the doc comment in front of it. */
  private static String declaration(String definitions, String name) {
    Matcher matcher =
        Pattern.compile(
                "(/\\*\\*(?:(?!\\*/).)*\\*/\\s*)?export interface " + name + "\\b.*?\n\t}",
                Pattern.DOTALL)
            .matcher(definitions);
    assertThat(matcher.find()).as("declaration of %s", name).isTrue();
    return matcher.group();
  }

  @Test
  public void documentedOverrideIsEmittedWithItsDocs() throws IOException {
    assertThat(declaration(definitions(), "DocumentedOverride"))
        .contains("Documented here.")
        .contains("name():string;");
  }

  @Test
  public void deprecatingOverrideIsEmittedDeprecated() throws IOException {
    assertThat(declaration(definitions(), "DeprecatedOverride"))
        .contains("@deprecated")
        .contains("name():string;");
  }

  // The override is deprecated too, by inheritance, so it restates the method exactly.
  @Test
  public void overrideOfDeprecatedMethodIsNotRedeclared() throws IOException {
    assertThat(declaration(definitions(), "RestatesDeprecated"))
        .doesNotContain("old()")
        .contains("other():string;");
  }
}
