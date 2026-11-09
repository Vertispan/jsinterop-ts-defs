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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import org.junit.Test;

/** Type arguments that inherited members are emitted with. */
public class InheritedTypeArgumentsTest {

  private static Compilation compile(String... names) {
    return Compiler.javac()
        .withProcessors(new JsTypesProcessor())
        .compile(
            Stream.of(names)
                .map(name -> JavaFileObjects.forResource("inherited/" + name + ".java"))
                .collect(Collectors.toList()));
  }

  private static final String HIDDEN_ARG =
      "A none public type [HiddenArg] is referenced but it will not be exported.";

  /** Reported once, on the class whose extends clause names it, though two classes merge it. */
  @Test
  public void nonPublicArgumentToIgnoredSuperclassIsReportedOnce() {
    Compilation result = compile("HiddenArg", "IgnoredBase", "HidesArg", "HidesArgSub");

    assertThat(result.errors())
        .singleElement()
        .satisfies(
            d -> {
              assertThat(d.getMessage(null)).isEqualTo(HIDDEN_ARG);
              assertThat(d.getSource().getName()).endsWith("HidesArg.java");
            });
  }

  @Test
  public void nonPublicArgumentInIgnoredClassIsReportedOnThatClass() {
    Compilation result = compile("HiddenArg", "IgnoredBase", "IgnoredMid", "MidUser");

    assertThat(result.errors())
        .singleElement()
        .satisfies(
            d -> {
              assertThat(d.getMessage(null)).isEqualTo(HIDDEN_ARG);
              assertThat(d.getSource().getName()).endsWith("IgnoredMid.java");
            });
  }

  @Test
  public void nonPublicArgumentToIgnoredInterfaceIsReported() {
    Compilation result = compile("HiddenArg", "IgnoredSource", "ImplementsIgnored");

    assertThat(result.errors())
        .singleElement()
        .satisfies(d -> assertThat(d.getMessage(null)).isEqualTo(HIDDEN_ARG));
  }

  @Test
  public void argumentNoEmittedMemberUsesIsNotReported() {
    Compilation result = compile("HiddenArg", "UnusedBase", "IgnoresUnused");

    assertThat(result.errors()).isEmpty();
  }

  /**
   * TypeScript has no single type for both, so it is an error, once, where the paths meet, though
   * an emitted subclass inherits the conflict.
   */
  @Test
  public void conflictingArgumentsThroughDiamondAreAnErrorOnce() {
    Compilation result =
        compile("Source", "NullableBranch", "PlainBranch", "Diamond", "DiamondSub");

    assertThat(result.errors())
        .singleElement()
        .satisfies(
            d -> {
              assertThat(d.getMessage(null))
                  .startsWith("Type parameter ")
                  .contains("T of Source", "NullableBranch", "PlainBranch", "Diamond");
              assertThat(d.getSource().getName()).endsWith("Diamond.java");
            });
  }

  // Where the paths meet is a @TsIgnore interface, not either of the emitted classes.
  @Test
  public void conflictingArgumentsAreReportedWhereThePathsMeet() {
    Compilation result =
        compile("Source", "NullableBranch", "PlainBranch", "Meet", "MeetUser1", "MeetUser2");

    assertThat(result.errors())
        .singleElement()
        .satisfies(d -> assertThat(d.getSource().getName()).endsWith("Meet.java"));
  }

  // The override chosen from OverridingBase uses T as String; the clause passes it nullable.
  @Test
  public void conflictHiddenByChosenOverrideIsReported() {
    Compilation result = compile("BoundedSource", "OverridingBase", "ViaOverride");

    assertThat(result.errors())
        .singleElement()
        .satisfies(
            d -> {
              assertThat(d.getMessage(null)).contains("T of BoundedSource");
              assertThat(d.getSource().getName()).endsWith("ViaOverride.java");
            });
  }

  @Test
  public void memberReachedTwiceWithSameArgumentIsEmittedOnce() throws IOException {
    Compilation result = compile("Source", "PlainBranch", "PassBranch", "SameTwice");

    assertThat(result.errors()).isEmpty();
    assertThat(result.warnings()).noneMatch(d -> d.getMessage(null).startsWith("Type parameter "));
    String output = output(result);
    int start = output.indexOf("class SameTwice");
    assertThat(start).as("SameTwice is emitted").isNotNegative();
    String body = output.substring(start, output.indexOf('}', start));
    assertThat(body.split("\\bget\\(", -1)).as("get() in %s", body).hasSize(2);
    assertThat(body).contains("get():string");
  }

  private static final JavaFileObject SRC3 =
      JavaFileObjects.forSourceString(
          "p.Src3",
          "package p; @jsinterop.annotations.JsType public interface Src3<T> {"
              + " T a(); T b(); void c(T t); }");

  private static final JavaFileObject PRODUCER =
      JavaFileObjects.forSourceString(
          "p.Producer",
          "package p; @jsinterop.annotations.JsType public interface Producer<T> { T make(); }");

  // The clause and each of the three copied members convert the argument.
  @Test
  public void wildcardWarningOnClauseArgumentIsReportedOnce() {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                SRC3,
                PRODUCER,
                JavaFileObjects.forSourceString(
                    "p.S1",
                    "package p; @jsinterop.annotations.JsType public abstract class S1"
                        + " implements Src3<Producer<? super String>> {}"));

    assertThat(result.warnings())
        .filteredOn(d -> d.getMessage(null).startsWith("Wildcard ["))
        .hasSize(1);
  }

  @Test
  public void unsupportedLiteralOnClauseArgumentIsReportedOnce() {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                SRC3,
                JavaFileObjects.forSourceString(
                    "p.S2",
                    "package p; @jsinterop.annotations.JsType public abstract class S2"
                        + " implements Src3<@com.vertispan.tsdefs.annotations.TsLiteral(\"x\")"
                        + " Object> {}"));

    assertThat(result.errors())
        .filteredOn(d -> d.getMessage(null).startsWith("@TsLiteral is not supported"))
        .hasSize(1);
  }

  private static Compilation compileSources(String... nameAndSource) {
    List<JavaFileObject> sources = new ArrayList<>();
    for (int i = 0; i < nameAndSource.length; i += 2) {
      sources.add(
          JavaFileObjects.forSourceString(
              "p." + nameAndSource[i], "package p; " + nameAndSource[i + 1]));
    }
    return Compiler.javac().withProcessors(new JsTypesProcessor()).compile(sources);
  }

  private static List<String> messages(List<? extends Diagnostic<?>> diagnostics) {
    return diagnostics.stream().map(d -> d.getMessage(null)).collect(Collectors.toList());
  }

  // The clause's wildcard warning is about Src's T on D, as is the diamond error.
  @Test
  public void diamondErrorIsReportedBesideAWildcardWarning() {
    Compilation result =
        compileSources(
            "Src", "@jsinterop.annotations.JsType public interface Src<T> { T get(); }",
            "NB",
                "@jsinterop.annotations.JsType public interface NB"
                    + " extends Src<@jsinterop.annotations.JsNullable String> {}",
            "PB", "@jsinterop.annotations.JsType public interface PB extends Src<String> {}",
            "Other", "@jsinterop.annotations.JsType public interface Other<T> { T other(); }",
            "D",
                "@jsinterop.annotations.JsType public abstract class D"
                    + " implements NB, PB, Other<Src<? super String>> {}");

    assertThat(messages(result.errors())).anyMatch(m -> m.startsWith("Type parameter T of Src"));
    assertThat(messages(result.warnings())).anyMatch(m -> m.startsWith("Wildcard ["));
  }

  @Test
  public void unsupportedLiteralAndNonPublicTypeAreBothReported() {
    Compilation result =
        compileSources(
            "Box", "@jsinterop.annotations.JsType public interface Box<T> { T get(); }",
            "Hidden", "class Hidden {}",
            "M",
                "@jsinterop.annotations.JsType public class M { public void m("
                    + "Box<@com.vertispan.tsdefs.annotations.TsLiteral(\"x\") Hidden> b) {} }");

    assertThat(messages(result.errors()))
        .anyMatch(m -> m.startsWith("@TsLiteral is not supported"))
        .anyMatch(m -> m.contains("[Hidden] is referenced"));
  }

  @Test
  public void differentWildcardsInOneClauseAreEachReported() {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                PRODUCER,
                JavaFileObjects.forSourceString(
                    "p.Two",
                    "package p; @jsinterop.annotations.JsType public interface Two<A, B> {"
                        + " A a(); B b(); }"),
                JavaFileObjects.forSourceString(
                    "p.S",
                    "package p; @jsinterop.annotations.JsType public abstract class S"
                        + " implements Two<Producer<? super String>, Producer<? super Integer>>"
                        + " {}"));

    assertThat(messages(result.warnings())).filteredOn(m -> m.startsWith("Wildcard [")).hasSize(2);
  }

  // The JsFunction's return type is converted again for each copied member using the argument.
  @Test
  public void wildcardInAJsFunctionClauseArgumentIsReportedOnce() {
    Compilation result =
        Compiler.javac()
            .withProcessors(new JsTypesProcessor())
            .compile(
                SRC3,
                JavaFileObjects.forSourceString(
                    "p.Box",
                    "package p; @jsinterop.annotations.JsType public interface Box<T> {"
                        + " T get(); }"),
                JavaFileObjects.forSourceString(
                    "p.WildFn",
                    "package p; @jsinterop.annotations.JsFunction public interface WildFn {"
                        + " Box<? super String> call(); }"),
                JavaFileObjects.forSourceString(
                    "p.S1",
                    "package p; @jsinterop.annotations.JsType public abstract class S1"
                        + " implements Src3<WildFn> {}"));

    assertThat(messages(result.warnings())).filteredOn(m -> m.startsWith("Wildcard [")).hasSize(1);
  }

  private static String output(Compilation result) throws IOException {
    return result
        .generatedFile(StandardLocation.CLASS_OUTPUT, "", "types.d.ts")
        .orElseThrow(AssertionError::new)
        .getCharContent(false)
        .toString();
  }

  /**
   * Each level has two interfaces extending both below it, so the top is reached by 2^depth paths.
   */
  // At depth 16 this takes about 2.5s; walking each path instead of each type takes about 12s.
  @Test(timeout = 6_000)
  public void diamondLatticeIsWalkedOncePerType() {
    int depth = 16;
    List<JavaFileObject> sources = new ArrayList<>();
    for (int level = 0; level <= depth; level++) {
      for (String side : new String[] {"A", "B"}) {
        String name = "L" + level + side;
        String supers =
            level == 0 ? "" : " extends L" + (level - 1) + "A<T>, L" + (level - 1) + "B<T>";
        String body = level == 0 ? "T get();" : "";
        sources.add(
            JavaFileObjects.forSourceString(
                "lattice." + name,
                "package lattice; @jsinterop.annotations.JsType public interface "
                    + name
                    + "<T>"
                    + supers
                    + " { "
                    + body
                    + " }"));
      }
    }
    sources.add(
        JavaFileObjects.forSourceString(
            "lattice.Top",
            "package lattice; @jsinterop.annotations.JsType public abstract class Top implements L"
                + depth
                + "A<String>, L"
                + depth
                + "B<String> {}"));

    Compilation result = Compiler.javac().withProcessors(new JsTypesProcessor()).compile(sources);

    assertThat(result.errors()).isEmpty();
  }
}
