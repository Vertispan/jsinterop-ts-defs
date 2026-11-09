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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.JavaFileObject;
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

  /** Warned once, where the paths meet, though a subclass inherits the conflict. */
  @Test
  public void conflictingArgumentsThroughDiamondWarnOnce() {
    Compilation result =
        compile("Source", "NullableBranch", "PlainBranch", "Diamond", "DiamondSub");

    assertThat(result.errors()).isEmpty();
    assertThat(result.warnings())
        .filteredOn(d -> d.getMessage(null).startsWith("Type parameter "))
        .singleElement()
        .satisfies(
            d -> {
              assertThat(d.getMessage(null))
                  .contains("T of Source", "NullableBranch", "PlainBranch", "Diamond");
              assertThat(d.getSource().getName()).endsWith("Diamond.java");
            });
  }

  /**
   * Each level has two interfaces extending both below it, so the top is reached by 2^depth paths.
   */
  @Test(timeout = 10_000)
  public void diamondLatticeIsWalkedOncePerType() {
    int depth = 12;
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
