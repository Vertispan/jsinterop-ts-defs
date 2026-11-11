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

import com.vertispan.tsdefs.impl.model.ArrayTsType;
import com.vertispan.tsdefs.impl.model.TsNullableType;
import com.vertispan.tsdefs.impl.model.TsType;
import com.vertispan.tsdefs.impl.model.TsUnionType;
import org.junit.Test;

/** Sets of methods, properties and union members merge only types that print the same. */
public class TsTypeEqualityTest {

  private static final TsType FOO = TsType.of("Foo", "");
  private static final TsType BAR = TsType.of("Bar", "");

  @Test
  public void anonymousUnionsDifferingInMembersAreNotEqual() {
    TsType fooOrString = TsUnionType.of(true, FOO, TsType.of("string", ""));
    TsType barOrString = TsUnionType.of(true, BAR, TsType.of("string", ""));

    assertThat(fooOrString).isNotEqualTo(barOrString);
    assertThat(fooOrString)
        .isEqualTo(TsUnionType.of(true, FOO, TsType.of("string", "")))
        .hasSameHashCodeAs(TsUnionType.of(true, FOO, TsType.of("string", "")));
  }

  @Test
  public void unionKeepsArraysOfDifferentElementTypes() {
    assertThat(TsUnionType.of(true, ArrayTsType.of(FOO), ArrayTsType.of(BAR)).emit(""))
        .isEqualTo("Foo[]|Bar[]");
  }

  @Test
  public void nullableTypeIsNotEqualToItsType() {
    assertThat(TsNullableType.of(FOO)).isNotEqualTo(FOO).isNotEqualTo(TsNullableType.of(BAR));
    assertThat(TsNullableType.of(FOO))
        .isEqualTo(TsNullableType.of(TsType.of("Foo", "")))
        .hasSameHashCodeAs(TsNullableType.of(TsType.of("Foo", "")));
  }
}
