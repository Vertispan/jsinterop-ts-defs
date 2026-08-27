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

import com.vertispan.tsdefs.impl.model.TsType;
import com.vertispan.tsdefs.impl.model.TsUnionType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class UnionTypesTest {

  @Test
  public void testMergeUnionTypes() {
    TsType tsType1 =
        TsUnionType.of(true, TsType.of("string"), TsType.of("number"), TsType.of("string"));
    TsType tsType2 = TsUnionType.of(true, tsType1, TsType.of("number"), TsType.of("string"));
    TsType tsType3 =
        TsUnionType.of(true, tsType1, tsType2, TsType.of("number"), TsType.of("string"));

    assertThat(tsType3.emit("")).isEqualTo("string|number");
  }

  @Test
  public void testLiteralUnionTypesPreserveOrder() {
    TsType tsType =
        TsUnionType.of(true, TsType.of("\"one\""), TsType.of("number"), TsType.of("\"two\""));

    assertThat(tsType.emit("")).isEqualTo("\"one\"|number|\"two\"");
  }

  @Test
  public void testExplicitUnionDefinitionsAndReferences() throws IOException {
    String definitions;
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream("types/index.d.ts")) {
      assertThat(stream).as("generated TypeScript definitions").isNotNull();
      definitions = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }

    assertThat(definitions)
        .contains(
            "/**\n*A named explicit parameter union. \n*/\ntype ExplicitParamUnion = number|Array<number|null|undefined>;")
        .contains("/**\n*A generic explicit union. \n*/\ntype ExplicitUnion<T> = number|Array<T>;")
        .contains("type ExplicitParamUnion = number|Array<number|null|undefined>;")
        .contains("type ExplicitUnion<T> = number|Array<T>;")
        .contains(
            "someFunction(param1:ExplicitParamUnion, param2:ExplicitParamUnion|null|undefined):ExplicitUnion<string>|null|undefined;")
        .doesNotContain("type ExplicitParamUnion = ExplicitParamUnion;")
        .doesNotContain("type ExplicitUnion = ExplicitUnion;");
  }

  @Test
  public void testExplicitUnionReferencesInReturnsAndNestedTypes() throws IOException {
    String definitions;
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream("types/index.d.ts")) {
      assertThat(stream).as("generated TypeScript definitions").isNotNull();
      definitions = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }

    assertThat(definitions)
        .contains("nonNullableUnionReturn():ExplicitParamUnion;")
        .contains("nullableUnionReturn():ExplicitParamUnion|null|undefined;")
        .contains(
            "genericUnionWithNamedArgument(value:ExplicitUnion<ExplicitParamUnion>):ExplicitUnion<ExplicitParamUnion>;")
        .contains("unionArrayReturn():Array<ExplicitParamUnion>;")
        .contains("nullableUnionArrayReturn():Array<ExplicitParamUnion>|null|undefined;")
        .contains("union2dArrayReturn():Array<Array<ExplicitParamUnion>>;")
        .contains(
            "nullableGenericUnionReturn(value:ExplicitUnion<Array<ExplicitParamUnion>>|null|undefined):ExplicitUnion<Array<ExplicitParamUnion>>|null|undefined;");
  }
}
