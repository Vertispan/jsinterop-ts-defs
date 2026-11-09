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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

/** An override wider in TypeScript than what it overrides, which TypeScript rejects. */
public class OverrideWideningTest {

  private static Compilation compile(String... names) {
    return Compiler.javac()
        .withProcessors(new JsTypesProcessor())
        .compile(
            Stream.of(names)
                .map(name -> JavaFileObjects.forResource("errors/overrides/" + name + ".java"))
                .collect(Collectors.toList()));
  }

  @Test
  public void overrideAddingNullableIsAnError() {
    assertThat(compile("NameBase", "NullableName").errors())
        .singleElement()
        .satisfies(
            d -> {
              assertThat(d.getMessage(null)).contains("NullableName.name()", "@JsNullable");
              assertThat(d.getSource().getName()).endsWith("NullableName.java");
            });
  }

  @Test
  public void overrideDroppingLiteralIsAnError() {
    assertThat(compile("LiteralBase", "DropsLiteral").errors())
        .singleElement()
        .satisfies(
            d -> {
              assertThat(d.getMessage(null)).contains("DropsLiteral.get()", "@TsLiteral");
              assertThat(d.getSource().getName()).endsWith("DropsLiteral.java");
            });
  }
}
