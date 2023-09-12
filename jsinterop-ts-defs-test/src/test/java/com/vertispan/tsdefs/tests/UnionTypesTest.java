/*
 * Copyright © 2023 Vertispan
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
import org.junit.Test;

public class UnionTypesTest {

  @Test
  public void testMergeUnionTypes() {
    TsType tsType1 = TsUnionType.of(TsType.of("string"), TsType.of("number"), TsType.of("string"));
    TsType tsType2 = TsUnionType.of(tsType1, TsType.of("number"), TsType.of("string"));
    TsType tsType3 = TsUnionType.of(tsType1, tsType2, TsType.of("number"), TsType.of("string"));

    assertThat(tsType3.emit("")).isEqualTo("string|number");
  }
}
