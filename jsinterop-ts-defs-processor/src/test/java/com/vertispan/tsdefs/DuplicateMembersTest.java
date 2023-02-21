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
package com.vertispan.tsdefs;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.vertispan.tsdefs.visitors.TypeVisitor;
import java.util.Locale;
import org.junit.Test;

public class DuplicateMembersTest {

  @Test
  public void testJsTypeWithDuplicateFieldAndMethodShouldFail() {

    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessorProcessor())
            .compile(
                JavaFileObjects.forResource(
                    "errors/members/JsTypeWithDuplicateMethodAndField.java"));

    assertThat(result.errors().size()).isEqualTo(1);
    assertThat(result.errors().get(0).getMessage(Locale.getDefault()))
        .isEqualTo(TypeVisitor.DUPLICATE_MEMBERS_ARE_NOT_ALLOWED);
  }

  @Test
  public void testJsTypeWithDuplicateFieldAndJsPropertyNamesShouldFail() {

    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessorProcessor())
            .compile(
                JavaFileObjects.forResource(
                    "errors/members/JsTypeWithDuplicateFieldAndJsPropertyName.java"));

    assertThat(result.errors().size()).isGreaterThan(0);
    assertThat(result.errors())
        .allMatch(
            diagnostic ->
                diagnostic
                    .getMessage(Locale.getDefault())
                    .equals(TypeVisitor.DUPLICATE_MEMBERS_ARE_NOT_ALLOWED));
  }

  @Test
  public void testJsTypeWithDuplicateJsPropertiesNamesShouldFail() {

    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessorProcessor())
            .compile(
                JavaFileObjects.forResource(
                    "errors/members/JsTypeWithDuplicateJsPropertiesName.java"));

    assertThat(result.errors().size()).isEqualTo(1);
    assertThat(result.errors().get(0).getMessage(Locale.getDefault()))
        .isEqualTo(TypeVisitor.DUPLICATE_MEMBERS_ARE_NOT_ALLOWED);
  }

  @Test
  public void testJsTypeWithPrivateFieldAndPublicAccessorJsPropertyShouldPass() {

    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessorProcessor())
            .compile(
                JavaFileObjects.forResource(
                    "errors/members/JsTypeWithPrivateFieldAndPublicAccessorJsProperty.java"));

    assertThat(result.errors()).isEmpty();
  }

  @Test
  public void testNoneJsTypeWithDuplicateMethodsNamesAndOnlyOneExportableShouldPass() {

    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessorProcessor())
            .compile(JavaFileObjects.forResource("errors/members/NoJsTypeDuplicateMethod.java"));

    assertThat(result.errors()).isEmpty();
  }

  @Test
  public void testJsTypeWithDuplicateStaticAndNoneStaticMethodsShouldPass() {

    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessorProcessor())
            .compile(
                JavaFileObjects.forResource(
                    "errors/members/JsTypeWithDuplicateStaticAndNoneStaticMethods.java"));

    assertThat(result.errors()).isEmpty();
  }

  @Test
  public void testJsTypeWithDuplicateStaticMembersShouldFail() {

    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessorProcessor())
            .compile(
                JavaFileObjects.forResource(
                    "errors/members/JsTypeWithDuplicateStaticMembers.java"));

    assertThat(result.errors().size()).isEqualTo(1);
    assertThat(result.errors().get(0).getMessage(Locale.getDefault()))
        .isEqualTo(TypeVisitor.DUPLICATE_MEMBERS_ARE_NOT_ALLOWED);
  }
}
