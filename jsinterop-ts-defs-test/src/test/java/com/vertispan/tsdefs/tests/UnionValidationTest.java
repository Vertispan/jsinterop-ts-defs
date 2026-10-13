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
import com.vertispan.tsdefs.impl.visitors.TypeVisitor;
import com.vertispan.tsdefs.processor.JsTypesProcessor;
import java.util.Locale;
import org.junit.Test;

public class UnionValidationTest {

  @Test
  public void namedAnonymousUnionShouldFail() {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(JavaFileObjects.forResource("errors/unions/NamedAnonymousUnion.java"));

    assertThat(result.errors()).hasSize(1);
    assertThat(result.errors().get(0).getMessage(Locale.getDefault()))
        .isEqualTo(TypeVisitor.UNION_NAME_ON_ANONYMOUS_UNION_NOT_ALLOWED);
  }

  @Test
  public void suppressedExplicitUnionShouldFail() {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(JavaFileObjects.forResource("errors/unions/SuppressedExplicitUnion.java"));

    assertThat(result.errors()).hasSize(1);
    assertThat(result.errors().get(0).getMessage(Locale.getDefault()))
        .isEqualTo(TypeVisitor.INVALID_EXPLICIT_UNION_NAME);
  }
}
