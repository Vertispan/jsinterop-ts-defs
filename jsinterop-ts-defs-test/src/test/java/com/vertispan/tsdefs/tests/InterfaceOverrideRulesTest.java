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

/**
 * An interface's own override is emitted under its JS name, unless TypeScript couldn't take it in
 * place of what it overrides; then the inherited declaration stands, with a warning naming the fix.
 */
public class InterfaceOverrideRulesTest {

  private static Compilation compile(String... names) {
    return Compiler.javac()
        .withProcessors(new JsTypesProcessor())
        .compile(
            Stream.of(names)
                .map(name -> JavaFileObjects.forResource("warnings/overrides/" + name + ".java"))
                .collect(Collectors.toList()));
  }

  private static String output(Compilation result) throws IOException {
    return result
        .generatedFile(StandardLocation.CLASS_OUTPUT, "", "types.d.ts")
        .orElseThrow(AssertionError::new)
        .getCharContent(false)
        .toString();
  }

  /** The declaration of {@code type} in the generated d.ts, up to its closing brace. */
  private static String declaration(String output, String type) {
    int start = output.indexOf(type);
    assertThat(start).as("%s is emitted", type).isNotNegative();
    return output.substring(start, output.indexOf('}', start));
  }

  // Unannotated overrides take the renamed members' JS names, size and describe. The getter is
  // emitted; text() narrows describe's object to string, which TypeScript can't take, so it is left
  // out. TODO(#144)
  @Test
  public void unannotatedOverridesOfRenamedMembersTakeTheirNames() throws IOException {
    Compilation result = compile("Shape", "NarrowCircle", "SameCircle", "AnnotatedCircle");

    assertThat(result.errors()).isEmpty();
    assertThat(result.warnings()).filteredOn(d -> d.getMessage(null).contains("Circle.")).isEmpty();
    assertThat(declaration(output(result), "interface NarrowCircle"))
        .contains("radius():number", "get size():number")
        .doesNotContain("describe(");
  }

  // Exported under the overridden method's JS name, x.
  @Test
  public void exportedOverrideOfARenamedMethodTakesItsName() throws IOException {
    Compilation result = compile("RenamedSource", "RenamedNarrowing");

    assertThat(result.errors()).isEmpty();
    assertThat(result.warnings())
        .filteredOn(d -> d.getMessage(null).contains("RenamedNarrowing."))
        .isEmpty();
    assertThat(output(result))
        .contains("x():RenamedNarrowing;", "other():number")
        .doesNotContain("foo()");
  }

  // The skip rules apply to renamed overrides too: foo() narrows x's object to string, left out
  // silently (TODO(#144)); bar() returns a type TypeScript sees as unknown, left out with the fix.
  @Test
  public void interfaceOverridesOfRenamedMethodsFollowTheSkipRules() throws IOException {
    Compilation result =
        compile("Item", "PlainItem", "RenamedPair", "RenamedPairSub", "TsRenamedPairSub");

    assertThat(result.errors()).isEmpty();
    assertThat(
            result.warnings().stream()
                .map(d -> d.getMessage(null))
                .filter(message -> message.contains("RenamedPairSub.")))
        .containsExactlyInAnyOrder(
            unknownWarning("RenamedPairSub"), unknownWarning("TsRenamedPairSub"));
    String output = output(result);
    for (String type : new String[] {"interface RenamedPairSub", "interface TsRenamedPairSub"}) {
      assertThat(declaration(output, type))
          .contains("other():number")
          .doesNotContain("foo(", "bar(", "x(", "y(");
    }
  }

  private static String unknownWarning(String type) {
    String override = type + ".bar()";
    return override
        + " returns warnings.overrides.PlainItem, which TypeScript sees as unknown, so it is left"
        + " out and RenamedPair.bar()'s declaration stands; return a type TypeScript can see, or"
        + " annotate "
        + override
        + " with @TsTypeRef to choose the emitted type.";
  }

  // Would not extend the overridden declaration in TypeScript: left out, with the fix.
  @Test
  public void interfaceOverrideTypeScriptCannotExtendIsLeftOutAndWarns() throws IOException {
    Compilation result = compile("Item", "PlainItem", "ItemSource", "PlainItemSource");

    assertThat(result.errors()).isEmpty();
    assertThat(
            result.warnings().stream()
                .map(d -> d.getMessage(null))
                .filter(message -> message.contains("is left out")))
        .containsExactlyInAnyOrder(
            "PlainItemSource.one() returns warnings.overrides.PlainItem, which TypeScript sees as"
                + " unknown, so it is left out and ItemSource.one()'s declaration stands; return a"
                + " type TypeScript can see, or annotate PlainItemSource.one() with @TsTypeRef to"
                + " choose the emitted type.",
            "PlainItemSource.all() returns warnings.overrides.PlainItem[], which TypeScript sees"
                + " as unknown[], so it is left out and ItemSource.all()'s declaration stands;"
                + " return a type TypeScript can see, or annotate PlainItemSource.all() with"
                + " @TsTypeRef to choose the emitted type.",
            "PlainItemSource.add() declares its parameters differently from ItemSource.add() in"
                + " TypeScript, so it is left out and ItemSource.add()'s declaration stands;"
                + " declare them as ItemSource.add() does.");
    assertThat(declaration(output(result), "interface PlainItemSource"))
        .contains("other():number")
        .doesNotContain("one(", "all(", "add(");
  }

  // A supertype between them already emits the narrower type under the JS name.
  @Test
  public void overrideOfANarrowingOverrideDoesNotWarn() {
    Compilation result = compile("RenamedText", "NarrowedText", "RestatedText", "BothTexts");

    assertThat(result.errors()).isEmpty();
    assertThat(result.warnings()).filteredOn(d -> d.getMessage(null).contains("Text.")).isEmpty();
  }
}
