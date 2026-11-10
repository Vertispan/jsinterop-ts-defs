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
 * An unannotated override of a renamed method or a property accessor is exported under the
 * inherited JS name, which the emitter doesn't model; a narrower type there warns.
 */
public class UnmodeledOverrideWarningTest {

  @Test
  public void narrowingUnannotatedOverrideWarnsWithTheFix() {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                Stream.of("Shape", "NarrowCircle", "SameCircle", "AnnotatedCircle")
                    .map(
                        name -> JavaFileObjects.forResource("warnings/overrides/" + name + ".java"))
                    .collect(Collectors.toList()));

    assertThat(result.errors()).isEmpty();
    assertThat(
            result.warnings().stream()
                .map(d -> d.getMessage(null))
                .filter(message -> message.contains("to emit the narrower type")))
        .containsExactlyInAnyOrder(
            "NarrowCircle.getSize() narrows the return type of Shape.getSize() to"
                + " java.lang.Double, but is exported under Shape.getSize()'s JS name, which isn't"
                + " modeled, so the declaration keeps Shape.getSize()'s type; annotate"
                + " NarrowCircle.getSize() with @JsProperty(name = \"size\") to emit the narrower"
                + " type.",
            "NarrowCircle.text() narrows the return type of Shape.text() to java.lang.String,"
                + " but is exported under Shape.text()'s JS name, which isn't modeled, so the"
                + " declaration keeps Shape.text()'s type; annotate NarrowCircle.text() with"
                + " @JsMethod(name = \"describe\") to emit the narrower type.");
  }

  // Exported, but under the overridden method's JS name: left out, with the fix.
  @Test
  public void exportedOverrideOfARenamedMethodIsLeftOutAndWarns() throws IOException {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                Stream.of("RenamedSource", "RenamedNarrowing")
                    .map(
                        name -> JavaFileObjects.forResource("warnings/overrides/" + name + ".java"))
                    .collect(Collectors.toList()));

    assertThat(result.errors()).isEmpty();
    assertThat(
            result.warnings().stream()
                .map(d -> d.getMessage(null))
                .filter(message -> message.contains("to emit the narrower type")))
        .containsExactly(
            "RenamedNarrowing.foo() narrows the return type of RenamedSource.foo() to"
                + " warnings.overrides.RenamedNarrowing, but is exported under"
                + " RenamedSource.foo()'s JS name, which isn't modeled, so the declaration keeps"
                + " RenamedSource.foo()'s type; annotate RenamedNarrowing.foo() with"
                + " @JsMethod(name = \"x\") to emit the narrower type.");
    String output =
        result
            .generatedFile(StandardLocation.CLASS_OUTPUT, "", "types.d.ts")
            .orElseThrow(AssertionError::new)
            .getCharContent(false)
            .toString();
    assertThat(output).contains("other():number").doesNotContain("foo()");
  }

  // Would not extend the overridden declaration in TypeScript: left out, with the fix.
  @Test
  public void interfaceOverrideTypeScriptCannotExtendIsLeftOutAndWarns() throws IOException {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                Stream.of("Item", "PlainItem", "ItemSource", "PlainItemSource")
                    .map(
                        name -> JavaFileObjects.forResource("warnings/overrides/" + name + ".java"))
                    .collect(Collectors.toList()));

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
    String output =
        result
            .generatedFile(StandardLocation.CLASS_OUTPUT, "", "types.d.ts")
            .orElseThrow(AssertionError::new)
            .getCharContent(false)
            .toString();
    String narrow = output.substring(output.indexOf("interface PlainItemSource"), output.length());
    narrow = narrow.substring(0, narrow.indexOf('}'));
    assertThat(narrow).contains("other():number").doesNotContain("one(", "all(", "add(");
  }

  // A supertype between them already emits the narrower type under the JS name.
  @Test
  public void overrideOfANarrowingOverrideDoesNotWarn() {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                Stream.of("RenamedText", "NarrowedText", "RestatedText", "BothTexts")
                    .map(
                        name -> JavaFileObjects.forResource("warnings/overrides/" + name + ".java"))
                    .collect(Collectors.toList()));

    assertThat(result.errors()).isEmpty();
    assertThat(result.warnings())
        .filteredOn(d -> d.getMessage(null).contains("to emit the narrower type"))
        .isEmpty();
  }
}
