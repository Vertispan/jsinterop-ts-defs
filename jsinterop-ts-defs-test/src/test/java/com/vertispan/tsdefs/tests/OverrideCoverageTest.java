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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.StandardLocation;
import org.junit.Test;

/** Which declarations of one method a type's TypeScript declaration ends up with. */
public class OverrideCoverageTest {

  private static Compilation compile(String... names) {
    return Compiler.javac()
        .withProcessors(new JsTypesProcessor())
        .compile(
            Stream.of(names)
                .map(name -> JavaFileObjects.forResource("coverage/" + name + ".java"))
                .collect(Collectors.toList()));
  }

  /** The declaration of {@code type} in the generated d.ts, up to its closing brace. */
  private static String declaration(Compilation result, String type) throws IOException {
    String output =
        result
            .generatedFile(StandardLocation.CLASS_OUTPUT, "", "types.d.ts")
            .orElseThrow(AssertionError::new)
            .getCharContent(false)
            .toString();
    int start = output.indexOf(" " + type + " ");
    assertThat(start).as("%s is emitted", type).isNotNegative();
    return output.substring(start, output.indexOf('}', start));
  }

  private static long leftOutWarnings(Compilation result) {
    return result.warnings().stream()
        .filter(d -> d.getMessage(null).contains("is left out"))
        .count();
  }

  // A method that TypeScript doesn't see, overridden by one it does, isn't copied next to it.
  @Test
  public void overriddenMethodOfAnIgnoredInterfaceIsNotCopied() throws IOException {
    Compilation result = compile("Foo", "IgnoredGetter", "NullableGetter", "NullableGetterImpl");

    assertThat(result.errors()).isEmpty();
    String impl = declaration(result, "NullableGetterImpl");
    assertThat(impl).contains("get():Foo|null|undefined;").doesNotContain("get():Foo;");
  }

  // Only an override that is emitted stands for the method it overrides.
  @Test
  public void methodOverriddenOnlyByAnIgnoredOverrideIsCopied() throws IOException {
    Compilation result = compile("IgnoredValue", "IgnoredOverride", "IgnoredOverrideImpl");

    assertThat(result.errors()).isEmpty();
    assertThat(declaration(result, "IgnoredOverrideImpl")).contains("v():object;");
  }

  // A native supertype is seen by TypeScript through its own declaration.
  @Test
  public void overrideOfANativeRestParameterIsLeftOutAndWarns() throws IOException {
    Compilation result = compile("NativeRest", "ArrayAdder");

    assertThat(leftOutWarnings(result)).isOne();
    assertThat(declaration(result, "ArrayAdder")).doesNotContain("add(");
  }

  // Type variables are compared by position, not by name.
  @Test
  public void overrideRenamingItsTypeVariableIsEmittedWithoutAWarning() throws IOException {
    Compilation result = compile("Converter", "DocumentedConverter");

    assertThat(leftOutWarnings(result)).isZero();
    assertThat(declaration(result, "DocumentedConverter"))
        .contains("Converts nothing.", "conv<Y>(y:Y):Y;");
  }

  // TypeScript method parameters are bivariant, so only a rest parameter has to match.
  @Test
  public void overrideWideningAParameterIsEmittedWithoutAWarning() throws IOException {
    Compilation result = compile("Taker", "NullTaker");

    assertThat(leftOutWarnings(result)).isZero();
    assertThat(declaration(result, "NullTaker"))
        .contains("Takes a string or null.", "take(s:string|null|undefined):void;");
  }
}
