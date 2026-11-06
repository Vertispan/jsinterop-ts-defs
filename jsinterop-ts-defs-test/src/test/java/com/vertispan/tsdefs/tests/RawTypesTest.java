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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.tools.JavaFileObject;
import org.junit.Test;

public class RawTypesTest {

  // A wildcard can't be a supertype's type argument in Java, so clauses suggest only Any.
  @Test
  public void rawImplementsClauseShouldFail() {
    assertSingleError(
        "errors/raw/RawImplements.java",
        "Raw type Container is not supported; give its type arguments,"
            + " e.g. Container<Any> (jsinterop.base.Any).");
  }

  @Test
  public void rawExtendsClauseShouldFail() {
    assertSingleError(
        "errors/raw/RawExtends.java",
        "Raw type GenericBase is not supported; give its type arguments,"
            + " e.g. GenericBase<Any> (jsinterop.base.Any).");
  }

  @Test
  public void rawMemberTypeShouldFail() {
    assertSingleError(
        "errors/raw/RawMember.java",
        "Raw type Container is not supported; give its type arguments,"
            + " e.g. Container<Any> (jsinterop.base.Any) or Container<?>.");
  }

  // Any is outside a bound, so a bounded type suggests only the wildcard, where Java allows one.
  @Test
  public void rawBoundedMemberTypeShouldFail() {
    assertSingleError(
        "errors/raw/RawBoundedMember.java",
        "Raw type Bounded is not supported; give its type arguments, e.g. Bounded<?>.");
  }

  @Test
  public void rawBoundedImplementsClauseShouldFail() {
    assertSingleError(
        "errors/raw/RawBoundedImplements.java",
        "Raw type Bounded is not supported; give its type arguments.");
  }

  // Inlined types: without the check, both crash looking up the missing type argument.
  @Test
  public void rawJsFunctionShouldFail() {
    assertSingleError(
        "errors/raw/RawGenericFnMember.java",
        "Raw type GenericFn is not supported; give its type arguments,"
            + " e.g. GenericFn<Any> (jsinterop.base.Any) or GenericFn<?>.");
  }

  @Test
  public void rawJsPropertyMapShouldFail() {
    assertSingleError(
        "errors/raw/RawPropertyMapMember.java",
        "Raw type JsPropertyMap is not supported; give its type arguments,"
            + " e.g. JsPropertyMap<Any> (jsinterop.base.Any) or JsPropertyMap<?>.");
  }

  @Test
  public void rawTypeWithSeveralParametersShouldFail() {
    assertSingleError(
        "errors/raw/RawPairMember.java",
        "Raw type Pair is not supported; give its type arguments,"
            + " e.g. Pair<Any, Any> (jsinterop.base.Any) or Pair<?, ?>.");
  }

  // One bounded parameter is enough to rule out Any.
  @Test
  public void rawTypeWithOneBoundedParameterShouldFail() {
    assertSingleError(
        "errors/raw/RawBoundedPairMember.java",
        "Raw type BoundedPair is not supported; give its type arguments,"
            + " e.g. BoundedPair<?, ?>.");
  }

  @Test
  public void differentRawTypesInOneMemberAreEachReported() {
    assertThat(compile("errors/raw/TwoRawTypesMember.java").errors())
        .extracting(e -> e.getMessage(Locale.getDefault()))
        .containsExactlyInAnyOrder(
            "Raw type Container is not supported; give its type arguments,"
                + " e.g. Container<Any> (jsinterop.base.Any) or Container<?>.",
            "Raw type GenericBase is not supported; give its type arguments,"
                + " e.g. GenericBase<Any> (jsinterop.base.Any) or GenericBase<?>.");
  }

  // Each raw use is reported once, in the file that writes it, however often it is emitted.
  @Test
  public void rawMemberInheritedIntoSeveralSubtypesIsReportedOnce() {
    assertErrorsOnlyIn(
        1, "RawInherited.java", "errors/raw/InheritsRaw1.java", "errors/raw/InheritsRaw2.java");
  }

  @Test
  public void rawTypesInJsFunctionAreReportedOnceOnTheJsFunction() {
    // The parameter and the return type are two raw uses.
    assertErrorsOnlyIn(2, "RawFn.java", "errors/raw/RawFn.java", "errors/raw/RawFnUser.java");
  }

  @Test
  public void rawSuperclassOfSkippedClassIsReportedOnce() {
    assertErrorsOnlyIn(
        1,
        "RawMid.java",
        "errors/raw/RawMid.java",
        "errors/raw/ExtendsMid1.java",
        "errors/raw/ExtendsMid2.java");
  }

  private static void assertSingleError(String resource, String message) {
    assertThat(compile(resource).errors())
        .extracting(e -> e.getMessage(Locale.getDefault()))
        .as("errors in %s", resource)
        .containsExactly(message);
  }

  private static void assertErrorsOnlyIn(int count, String file, String... resources) {
    assertThat(compile(resources).errors())
        .extracting(e -> e.getSource().getName() + ": " + e.getMessage(Locale.getDefault()))
        .as("errors expected only in %s", file)
        .hasSize(count)
        .allMatch(error -> error.contains("/" + file + ": "));
  }

  private static Compilation compile(String... resources) {
    List<JavaFileObject> files = new ArrayList<>();
    files.add(JavaFileObjects.forResource("errors/raw/Container.java"));
    files.add(JavaFileObjects.forResource("errors/raw/GenericBase.java"));
    files.add(JavaFileObjects.forResource("errors/raw/Bounded.java"));
    files.add(JavaFileObjects.forResource("errors/raw/Pair.java"));
    files.add(JavaFileObjects.forResource("errors/raw/BoundedPair.java"));
    files.add(JavaFileObjects.forResource("errors/raw/GenericFn.java"));
    for (String resource : resources) {
      files.add(JavaFileObjects.forResource(resource));
    }
    return Compiler.javac().withProcessors(new JsTypesProcessor()).compile(files);
  }
}
