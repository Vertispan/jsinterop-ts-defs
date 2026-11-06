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
import org.junit.Test;

public class RawTypesTest {

  @Test
  public void rawImplementsClauseShouldFail() {
    assertSingleRawTypeError("errors/raw/RawImplements.java", "Container");
  }

  @Test
  public void rawExtendsClauseShouldFail() {
    assertSingleRawTypeError("errors/raw/RawExtends.java", "GenericBase");
  }

  @Test
  public void rawMemberTypeShouldFail() {
    assertSingleRawTypeError("errors/raw/RawMember.java", "Container");
  }

  private static void assertSingleRawTypeError(String resource, String rawType) {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                JavaFileObjects.forResource("errors/raw/Container.java"),
                JavaFileObjects.forResource("errors/raw/GenericBase.java"),
                JavaFileObjects.forResource(resource));

    assertThat(result.errors())
        .extracting(e -> e.getMessage(Locale.getDefault()))
        .as("errors for raw %s in %s", rawType, resource)
        .containsExactly(
            "Raw type "
                + rawType
                + " is not supported; give its type arguments, e.g. "
                + rawType
                + "<Any> (jsinterop.base.Any) or "
                + rawType
                + "<?>.");
  }
}
