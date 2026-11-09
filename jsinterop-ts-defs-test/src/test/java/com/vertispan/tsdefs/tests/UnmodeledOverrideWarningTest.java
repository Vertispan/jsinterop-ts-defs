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
}
