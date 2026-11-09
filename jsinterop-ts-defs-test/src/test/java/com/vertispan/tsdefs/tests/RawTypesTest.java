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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.tools.JavaFileObject;
import org.junit.Test;

public class RawTypesTest {

  // Java forbids a wildcard as a supertype's own type argument, so clauses suggest only Any.
  @Test
  public void rawImplementsClauseShouldFail() {
    assertSingleError(
        "errors/raw/RawImplements.java",
        "Raw type Container in the supertypes of RawImplements is not supported;"
            + " give its type arguments,"
            + " e.g. Container<Any> (jsinterop.base.Any).");
  }

  @Test
  public void rawExtendsClauseShouldFail() {
    assertSingleError(
        "errors/raw/RawExtends.java",
        "Raw type GenericBase in the supertypes of RawExtends is not supported;"
            + " give its type arguments, e.g. GenericBase<Any> (jsinterop.base.Any).");
  }

  // Named with its enclosing types, so the suggestion can be pasted.
  @Test
  public void rawNestedTypeShouldFail() {
    assertThat(compile("errors/raw/Holder.java", "errors/raw/RawNestedMember.java").errors())
        .extracting(e -> e.getMessage(Locale.getDefault()))
        .containsExactly(
            "Raw type Holder.Inner in RawNestedMember.raw is not supported;"
                + " give its type arguments,"
                + " e.g. Holder.Inner<Any> (jsinterop.base.Any) or Holder.Inner<?>.");
  }

  // Reported as the union itself, not as a member type erased by the raw use.
  @Test
  public void rawImplicitUnionShouldFail() {
    assertSingleError(
        "errors/raw/RawUnionMember.java",
        "Raw type GenericUnion in RawUnionMember.raw is not supported; give its type arguments,"
            + " e.g. GenericUnion<Any> (jsinterop.base.Any) or GenericUnion<?>.");
  }

  // This and the next two: the raw supertype isn't emitted, but its members are merged in.
  @Test
  public void rawTsIgnoredSuperclassShouldFail() {
    assertSingleErrorIn(
        "RawExtendsIgnored.java",
        "Raw type IgnoredGenericBase in the supertypes of RawExtendsIgnored is not supported;"
            + " give its type arguments, e.g. IgnoredGenericBase<Any> (jsinterop.base.Any).",
        "errors/raw/IgnoredGenericBase.java",
        "errors/raw/RawExtendsIgnored.java");
  }

  // Reported though no emitted member uses T, as for an emitted raw clause.
  @Test
  public void rawTsIgnoredSuperclassWithUnusedParameterShouldFail() {
    assertSingleErrorIn(
        "RawExtendsUnused.java",
        "Raw type UnusedParamBase in the supertypes of RawExtendsUnused is not supported;"
            + " give its type arguments, e.g. UnusedParamBase<Any> (jsinterop.base.Any).",
        "errors/raw/UnusedParamBase.java",
        "errors/raw/RawExtendsUnused.java");
  }

  @Test
  public void rawSkippedGenericSuperclassShouldFail() {
    assertSingleErrorIn(
        "RawExtendsGenericMid.java",
        "Raw type GenericMid in the supertypes of RawExtendsGenericMid is not supported;"
            + " give its type arguments, e.g. GenericMid<Any> (jsinterop.base.Any).",
        "errors/raw/GenericMid.java",
        "errors/raw/RawExtendsGenericMid.java");
  }

  @Test
  public void rawTsIgnoredSuperinterfaceShouldFail() {
    assertSingleErrorIn(
        "RawExtendsIgnoredInterface.java",
        "Raw type IgnoredContainer in the supertypes of RawExtendsIgnoredInterface is not"
            + " supported; give its type arguments,"
            + " e.g. IgnoredContainer<Any> (jsinterop.base.Any).",
        "errors/raw/IgnoredContainer.java",
        "errors/raw/RawExtendsIgnoredInterface.java");
  }

  @Test
  public void rawMemberTypeShouldFail() {
    assertSingleError(
        "errors/raw/RawMember.java",
        "Raw type Container in RawMember.raw is not supported; give its type arguments,"
            + " e.g. Container<Any> (jsinterop.base.Any) or Container<?>.");
  }

  // Any is outside a bound, so a bounded type suggests only the wildcard, where Java allows one.
  @Test
  public void rawBoundedMemberTypeShouldFail() {
    assertSingleError(
        "errors/raw/RawBoundedMember.java",
        "Raw type Bounded in RawBoundedMember.raw is not supported; give its type arguments,"
            + " e.g. Bounded<?>.");
  }

  @Test
  public void rawBoundedImplementsClauseShouldFail() {
    assertSingleError(
        "errors/raw/RawBoundedImplements.java",
        "Raw type Bounded in the supertypes of RawBoundedImplements is not supported;"
            + " give its type arguments.");
  }

  // This and the next test: inlined types, which without the check crash looking up the missing
  // type argument.
  @Test
  public void rawJsFunctionShouldFail() {
    assertSingleError(
        "errors/raw/RawGenericFnMember.java",
        "Raw type GenericFn in parameter fn of RawGenericFnMember.take is not supported;"
            + " give its type arguments,"
            + " e.g. GenericFn<Any> (jsinterop.base.Any) or GenericFn<?>.");
  }

  @Test
  public void rawJsPropertyMapShouldFail() {
    assertSingleError(
        "errors/raw/RawPropertyMapMember.java",
        "Raw type JsPropertyMap in RawPropertyMapMember.raw is not supported;"
            + " give its type arguments,"
            + " e.g. JsPropertyMap<Any> (jsinterop.base.Any) or JsPropertyMap<?>.");
  }

  @Test
  public void rawTypeWithSeveralParametersShouldFail() {
    assertSingleError(
        "errors/raw/RawPairMember.java",
        "Raw type Pair in RawPairMember.raw is not supported; give its type arguments,"
            + " e.g. Pair<Any, Any> (jsinterop.base.Any) or Pair<?, ?>.");
  }

  // One bounded parameter is enough to rule out Any.
  @Test
  public void rawTypeWithOneBoundedParameterShouldFail() {
    assertSingleError(
        "errors/raw/RawBoundedPairMember.java",
        "Raw type BoundedPair in RawBoundedPairMember.raw is not supported;"
            + " give its type arguments,"
            + " e.g. BoundedPair<?, ?>.");
  }

  @Test
  public void differentRawTypesInOneMemberAreEachReported() {
    assertThat(compile("errors/raw/TwoRawTypesMember.java").errors())
        .extracting(e -> e.getMessage(Locale.getDefault()))
        .containsExactlyInAnyOrder(
            "Raw type Container in TwoRawTypesMember.both is not supported;"
                + " give its type arguments,"
                + " e.g. Container<Any> (jsinterop.base.Any) or Container<?>.",
            "Raw type GenericBase in TwoRawTypesMember.both is not supported;"
                + " give its type arguments,"
                + " e.g. GenericBase<Any> (jsinterop.base.Any) or GenericBase<?>.");
  }

  // The message says where the raw type is written, for when the diagnostic has no position.
  @Test
  public void rawErrorsNameWhereTheyAreWritten() {
    assertRawContainerLocations(
        Arrays.asList("errors/raw/RawPositions.java"),
        "in RawPositions.field",
        "in parameter ctorParam of the RawPositions constructor",
        "in the bound of type parameter T of RawPositions",
        "in the bound of type parameter U of RawPositions.generic",
        "in RawPositions.Nested.nestedField");
    assertRawContainerLocations(
        Arrays.asList("errors/raw/RawFn.java", "errors/raw/RawFnUser.java"),
        "in RawFn.apply",
        "in parameter c of RawFn.apply");
    assertRawContainerLocations(
        Arrays.asList("errors/raw/RawInUnion.java", "errors/raw/RawInUnionUser1.java"),
        "in RawInUnion.asContainer");
  }

  private static void assertRawContainerLocations(List<String> resources, String... locations) {
    assertThat(compile(resources.toArray(new String[0])).errors())
        .extracting(e -> e.getMessage(Locale.getDefault()))
        .containsExactlyInAnyOrder(
            Arrays.stream(locations)
                .map(
                    location ->
                        "Raw type Container "
                            + location
                            + " is not supported; give its type arguments,"
                            + " e.g. Container<Any> (jsinterop.base.Any) or Container<?>.")
                .toArray(String[]::new));
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

  // The same through the walk up to a @TsInterface superclass, which becomes an implements clause.
  @Test
  public void rawTsInterfaceSuperclassOfSkippedClassIsReportedOnce() {
    assertErrorsOnlyIn(
        1,
        "RawTsInterfaceMid.java",
        "errors/raw/GenericTsInterfaceBase.java",
        "errors/raw/RawTsInterfaceMid.java",
        "errors/raw/ExtendsTsInterfaceMid1.java",
        "errors/raw/ExtendsTsInterfaceMid2.java");
  }

  @Test
  public void rawInterfaceExtendsClauseShouldFail() {
    assertSingleError(
        "errors/raw/RawSubInterface.java",
        "Raw type Container in the supertypes of RawSubInterface is not supported;"
            + " give its type arguments,"
            + " e.g. Container<Any> (jsinterop.base.Any).");
  }

  // Each error is on the line of the member, parameter or declaration that writes the raw type.
  @Test
  public void rawErrorsPointAtTheLineThatWritesThem() {
    assertErrorLines(Arrays.asList("errors/raw/RawMember.java"), "RawMember.java:8");
    assertErrorLines(Arrays.asList("errors/raw/RawImplements.java"), "RawImplements.java:7");
    assertErrorLines(Arrays.asList("errors/raw/RawSubInterface.java"), "RawSubInterface.java:7");
    // The JsFunction's return type on its method, its parameter on the parameter.
    assertErrorLines(
        Arrays.asList("errors/raw/RawFn.java", "errors/raw/RawFnUser.java"),
        "RawFn.java:8",
        "RawFn.java:9");
  }

  // A union member is written in the union, like a JsFunction's signature in the function.
  @Test
  public void rawTypeInUnionMemberIsReportedOnceOnTheMember() {
    assertErrorLines(
        Arrays.asList(
            "errors/raw/RawInUnion.java",
            "errors/raw/RawInUnionUser1.java",
            "errors/raw/RawInUnionUser2.java"),
        "RawInUnion.java:21");
  }

  // The same raw type in two members of one type is two uses.
  @Test
  public void sameRawTypeInTwoMembersIsReportedForEach() {
    assertErrorLines(
        Arrays.asList("errors/raw/SameRawTwice.java"),
        "SameRawTwice.java:8",
        "SameRawTwice.java:10");
  }

  private static void assertErrorLines(List<String> resources, String... expected) {
    assertThat(compile(resources.toArray(new String[0])).errors())
        .extracting(e -> e.getSource().getName().replaceAll(".*/", "") + ":" + e.getLineNumber())
        .containsExactlyInAnyOrder(expected);
  }

  private static void assertSingleError(String resource, String message) {
    assertThat(compile(resource).errors())
        .extracting(e -> e.getMessage(Locale.getDefault()))
        .as("errors in %s", resource)
        .containsExactly(message);
  }

  private static void assertSingleErrorIn(String file, String message, String... resources) {
    assertThat(compile(resources).errors())
        .extracting(
            e ->
                e.getSource().getName().replaceAll(".*/", "")
                    + ": "
                    + e.getMessage(Locale.getDefault()))
        .containsExactly(file + ": " + message);
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
    files.add(JavaFileObjects.forResource("errors/raw/GenericUnion.java"));
    for (String resource : resources) {
      files.add(JavaFileObjects.forResource(resource));
    }
    return Compiler.javac().withProcessors(new JsTypesProcessor()).compile(files);
  }
}
