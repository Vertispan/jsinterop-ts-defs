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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * A {@code ? super X} wildcard on a type that produces its parameter warns; other wildcards do not.
 */
public class WildcardVarianceWarningTest {
  private static final String ITEM = "com.vertispan.tsdefs.tests.warnings.wildcards.Item";
  private static final Pattern WARNING =
      Pattern.compile(
          "^Wildcard \\[\\? super "
              + ITEM
              + "\\] is emitted as \\["
              + ITEM
              + "\\]( in \\w+ \\(inherited from \\w+\\))?, but (\\S+) produces its type parameter T, so TypeScript callers may receive a supertype of "
              + ITEM
              + "\\. Annotate the wildcard with @TsTypeRef to choose the emitted type\\.$");

  @Test
  public void superWildcardOnProducingTypeWarns() {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                Stream.of(
                        "Item",
                        "Producer",
                        "Consumer",
                        "Holder",
                        "SubProducer",
                        "Supplier",
                        "Mixed",
                        "Uses",
                        "SubUses",
                        "UsesClass",
                        "SubUsesClass",
                        "Hidden",
                        "Shown")
                    .map(
                        name -> JavaFileObjects.forResource("warnings/wildcards/" + name + ".java"))
                    .collect(Collectors.toList()));

    assertThat(result.errors()).isEmpty();
    List<String> warnings =
        result.warnings().stream()
            .map(d -> d.getMessage(null))
            .filter(w -> w.startsWith("Wildcard ["))
            .collect(Collectors.toList());

    // Reduce each warning to "producer" or "producer in Sub (inherited from Base)".
    List<String> actual =
        warnings.stream()
            .map(
                w -> {
                  Matcher m = WARNING.matcher(w);
                  assertThat(m.matches()).as(w).isTrue();
                  return m.group(2) + (m.group(1) == null ? "" : m.group(1));
                })
            .collect(Collectors.toList());

    assertThat(actual)
        .containsExactlyInAnyOrder(
            // Declared in Uses: direct method, field, inherited member, JsFunction, JsPropertyMap,
            // property getter.
            "Holder.value",
            "JsPropertyMap.get",
            "Producer.get",
            "Producer.get",
            "SubProducer.get",
            "Supplier.get",
            // The same six, re-emitted into SubUses.
            "Holder.value in SubUses (inherited from Uses)",
            "JsPropertyMap.get in SubUses (inherited from Uses)",
            "Producer.get in SubUses (inherited from Uses)",
            "Producer.get in SubUses (inherited from Uses)",
            "SubProducer.get in SubUses (inherited from Uses)",
            "Supplier.get in SubUses (inherited from Uses)",
            // UsesClass: field and getter; the getter again in SubUsesClass.
            "Producer.get",
            "Producer.get",
            "Producer.get in SubUsesClass (inherited from UsesClass)",
            // Hidden is @TsIgnore, so its member only appears merged into Shown.
            "Producer.get in Shown (inherited from Hidden)");
  }
}
