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
package com.vertispan.tsdefs.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.vertispan.tsdefs.processor.JsTypesProcessor;
import java.util.Locale;
import org.junit.Test;

public class InlinedFunctionTest {

  @Test
  public void testInlinedFunction() {

    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(JavaFileObjects.forResource("errors/functions/UsesIFunction.java"));

    assertThat(result.errors()).isEmpty();
  }

  // Reported once, on the function's method, however often the function is used.
  @Test
  public void jsFunctionMethodTypeParametersShouldFail() {
    assertSingleTypeParametersError("UsesGenericMethodFunction", 15);
  }

  // Reported even when no JsType uses the function.
  @Test
  public void unusedJsFunctionMethodTypeParametersShouldFail() {
    assertSingleTypeParametersError("UnusedGenericMethodFn", 8);
  }

  private static void assertSingleTypeParametersError(String name, long methodLine) {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(JavaFileObjects.forResource("errors/functions/" + name + ".java"));

    assertThat(result.errors())
        .singleElement()
        .satisfies(
            e -> {
              assertThat(e.getMessage(Locale.getDefault()))
                  .isEqualTo(
                      "JsFunction method apply declares type parameters, which are not supported.");
              assertThat(e.getLineNumber()).isEqualTo(methodLine);
            });
  }
}
