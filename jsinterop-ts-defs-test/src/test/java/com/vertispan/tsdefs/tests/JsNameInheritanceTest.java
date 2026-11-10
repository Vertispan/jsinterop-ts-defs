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

/** An override without a JS name of its own is exported under the overridden method's. */
public class JsNameInheritanceTest {

  private static Compilation compile(String... names) {
    return Compiler.javac()
        .withProcessors(new JsTypesProcessor())
        .compile(
            Stream.of(names)
                .map(name -> JavaFileObjects.forResource("names/" + name + ".java"))
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

  // GWT's JsInteropRestrictionCheckerTest.testOverrideNoNameSucceeds.
  @Test
  public void classOverridesTakeTheOverriddenName() throws IOException {
    Compilation result = compile("Parent", "Child1", "Child2");

    assertThat(result.errors()).isEmpty();
    for (String child : new String[] {"Child1", "Child2"}) {
      assertThat(declaration(result, child))
          .contains("a():void;", "b():void;")
          .doesNotContain("ma(", "mb(");
    }
  }

  @Test
  public void interfaceOverridesOfAnIgnoredInterfaceTakeItsNames() throws IOException {
    Compilation result = compile("IgnoredNames", "NamesFromIgnored");

    assertThat(result.errors()).isEmpty();
    assertThat(result.warnings())
        .filteredOn(d -> d.getMessage(null).contains("NamesFromIgnored"))
        .isEmpty();
    assertThat(declaration(result, "NamesFromIgnored"))
        .contains("x():string;", "y():string;", "other():number;")
        .doesNotContain("foo(", "same(");
  }
}
