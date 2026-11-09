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
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

/** {@code @TsDefault} values that TypeScript would reject are compile errors. */
public class TsDefaultValidationTest {

  private static Compilation compile(String... names) {
    return Compiler.javac()
        .withProcessors(new JsTypesProcessor())
        .compile(
            Stream.concat(Stream.of("Item", "SpecialItem"), Stream.of(names))
                .map(name -> JavaFileObjects.forResource("errors/defaults/" + name + ".java"))
                .collect(Collectors.toList()));
  }

  private static void assertSingleError(Compilation result, String message) {
    assertThat(result.errors())
        .extracting(d -> d.getMessage(Locale.getDefault()))
        .containsExactly(message);
  }

  @Test
  public void typeParameterWithoutDefaultAfterDefaultedOneFails() {
    assertSingleError(
        compile("OutOfOrder"),
        "Type parameter B of errors.defaults.OutOfOrder has no @TsDefault, but follows one that"
            + " does. TypeScript only allows defaults on trailing type parameters.");
  }

  @Test
  public void methodTypeParameterWithoutDefaultAfterDefaultedOneFails() {
    assertSingleError(
        compile("OutOfOrderMethod"),
        "Type parameter B of errors.defaults.OutOfOrderMethod.pick has no @TsDefault, but follows"
            + " one that does. TypeScript only allows defaults on trailing type parameters.");
  }

  // A class literal can't carry type arguments; reported as such, not as a raw type.
  @Test
  public void genericDefaultFails() {
    assertSingleError(
        compile("GenericBox", "GenericDefault"),
        "@TsDefault(errors.defaults.GenericBox) on type parameter T of"
            + " errors.defaults.GenericDefault names a generic type, which a default can't give"
            + " type arguments to.");
  }

  @Test
  public void defaultOutsideBoundFails() {
    assertSingleError(
        compile("OutsideBound"),
        "@TsDefault(errors.defaults.Item) on type parameter T of errors.defaults.OutsideBound does"
            + " not satisfy its bound errors.defaults.SpecialItem.");
  }
}
