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
package com.vertispan.tsdefs.tests.tsdocs.doclet;

import static org.assertj.core.api.Assertions.assertThat;

import com.vertispan.tsdefs.doclet.TsDoclet;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class DocletTest {

  /** A type's declaration, e.g. {@code "interface Foo"}, with its members and their docs. */
  private static String declaration(String definitions, String kindAndName) {
    Matcher matcher =
        Pattern.compile("export " + kindAndName + "\\b.*?\n\t}", Pattern.DOTALL)
            .matcher(definitions);
    if (!matcher.find()) {
      throw new AssertionError(kindAndName + " is not emitted");
    }
    return matcher.group();
  }

  private void testDocs(String subpackage) throws IOException {
    DocumentationTool systemDocumentationTool = ToolProvider.getSystemDocumentationTool();
    String[] args =
        new String[] {
          "-sourcepath",
          "src/test/java",
          "-subpackages",
          "com.vertispan.tsdefs.tests.tsdocs.doclet." + subpackage,
          TsDoclet.TSOUT_DIR,
          "target/test-classes/" + subpackage.replace(".", "/")
        };
    DocumentationTool.DocumentationTask task =
        systemDocumentationTool.getTask(
            null, null, null, TsDoclet.class, Arrays.asList(args), null);
    task.call();
    Files.copy(
        Paths.get("src/test/resources/tsconfig.json"),
        Paths.get("target/test-classes/" + subpackage.replace(".", "/"), "tsconfig.json"),
        StandardCopyOption.REPLACE_EXISTING);
    generateTypeDocsHtml(subpackage);
  }

  private void generateTypeDocsHtml(String subpackage) throws IOException {
    ProcessBuilder process =
        new ProcessBuilder(
            "npx", "typedoc", "types.d.ts", "-out", "documentation", "--skipErrorChecking");
    process
        .directory(Paths.get("target/test-classes/" + subpackage.replace(".", "/")).toFile())
        .start();
  }

  @Test
  public void testTypesLinks() throws IOException {
    testDocs("links.types");

    String definitions =
        Files.readString(
            Paths.get("target/test-classes/links/types/types.d.ts"), StandardCharsets.UTF_8);
    assertThat(definitions)
        .as("an unresolved link keeps its javadoc signature, unlabeled")
        .contains("Testing a link to none existing type {@link JsTypeNotExist}");
  }

  @Test
  public void testMethodsLinks() throws IOException {
    testDocs("links.methods");
  }

  @Test
  public void testIssue99() throws IOException {
    testDocs("links.issue99");
  }

  @Test
  public void testIssue104() throws IOException {
    testDocs("links.issue104");
  }

  @Test
  public void testIssue106() throws IOException {
    testDocs("links.issue106");
  }

  @Test
  public void testIssue116() throws IOException {
    testDocs("links.issue116");
  }

  @Test
  public void testIssue117() throws IOException {
    testDocs("docs.issue117");
  }

  @Test
  public void testIssue136() throws IOException {
    testDocs("links.issue136");

    String definitions =
        Files.readString(
            Paths.get("target/test-classes/links/issue136/types.d.ts"), StandardCharsets.UTF_8);

    SoftAssertions softly = new SoftAssertions();
    softly
        .assertThat(declaration(definitions, "class Foo"))
        .as("links from Foo to its own fields")
        .contains("{@link probe.Foo.BAR | Foo.BAR}")
        .contains("{@link probe.Foo.BAZ | Foo.BAZ}");
    softly
        .assertThat(declaration(definitions, "class Bar"))
        .as("links from Bar to Foo's fields")
        .contains("{@link probe.Foo.BAR | Foo.BAR}")
        .contains("{@link probe.Foo.BAZ | Foo.BAZ}");
    softly
        .assertThat(definitions)
        .as("a field's declared namespace, not its type's path, is the link's owner")
        .contains("Declared elsewhere: {@link probe.Other.QUX | Other.QUX}.");
    softly.assertAll();
  }

  @Test
  public void testIssue139() throws IOException {
    testDocs("links.issue139");

    String definitions =
        Files.readString(
            Paths.get("target/test-classes/links/issue139/types.d.ts"), StandardCharsets.UTF_8);

    SoftAssertions softly = new SoftAssertions();
    // #baz() and #baz render alike, so each contains() covers both forms.
    softly
        .assertThat(declaration(definitions, "class Foo"))
        .as("links from Foo to its own methods")
        .contains("{@link probe.Foo.baz | Foo.baz()}")
        .contains("{@link probe.Foo.scale | Foo.scale()}")
        .contains("{@link probe.Foo.BAR | Foo.BAR}")
        .contains("{@link probe.Bar | Bar}");
    softly
        .assertThat(declaration(definitions, "class Bar"))
        .as("links from Bar to Foo's methods")
        .contains("{@link probe.Foo.baz | Foo.baz()}")
        .contains("{@link probe.Foo.BAR | Foo.BAR}")
        .contains("{@link probe.Foo | Foo}");
    softly.assertAll();
  }

  @Test
  public void testIssue158() throws IOException {
    testDocs("links.issue158");

    String definitions =
        Files.readString(
            Paths.get("target/test-classes/links/issue158/types.d.ts"), StandardCharsets.UTF_8);

    SoftAssertions softly = new SoftAssertions();
    // The global class is not indented, so declaration() can't find it; match whole sentences.
    softly
        .assertThat(definitions)
        .as("links from the global class probe to Target")
        .contains(
            "Links from the global class: method {@link probe.Target.go | Target.go()}, field"
                + " {@link probe.Target.BAR | Target.BAR}, class {@link probe.Target | Target}.");
    softly
        .assertThat(definitions)
        .as("links from Target to the global class probe")
        .contains(
            "Links to the global class: method {@link probe.all | probe.all()}, class {@link"
                + " probe | probe}.");
    softly.assertAll();
  }

  @Test
  public void testLinkLabels() throws IOException {
    testDocs("links.labels");

    String definitions =
        Files.readString(
            Paths.get("target/test-classes/links/labels/types.d.ts"), StandardCharsets.UTF_8);

    SoftAssertions softly = new SoftAssertions();
    softly
        .assertThat(declaration(definitions, "class Source"))
        .as("unlabeled links from Source get the JS type and member name")
        .contains("{@link probe.Target.go | Target.go()}")
        .contains("{@link probe.scale | probe.scale()}")
        .contains("{@link probe.Target.BAR | Target.BAR}")
        .contains("{@link probe.Target.size | Target.size}")
        .contains("{@link probe.Target | Target}")
        .contains("{@link probe.all | probe.all()}")
        .contains("{@link probe.Target.go | the starter}");
    softly
        .assertThat(definitions)
        .as("a link to a setter names the property, without parentheses")
        .contains("Setter: {@link probe.Target.size | Target.size}.");
    softly
        .assertThat(definitions)
        .as("a link to an accessor with a declared name uses that name as written")
        .contains(
            "Accessor with a declared name: {@link probe.Target.settings | Target.settings}.");
    softly
        .assertThat(definitions)
        .as("a link to a constructor links to its type")
        .contains("Constructor: {@link probe.Target | Target}.");
    // TODO: on JDK 17+, put text after the </b>; JDK 11's parser drops the space before it.
    softly
        .assertThat(definitions)
        .as("a label containing HTML is kept whole")
        .contains("HTML label: {@link probe.Target.go | start <b>bold</b>}.");
    // Only a test while the formatter keeps the fixture's label wrapped, as for @see below.
    softly
        .assertThat(definitions)
        .as("a label wrapped across javadoc lines is emitted on one line")
        .contains(
            "Wrapped label: {@link probe.Target.go | a label long enough that the formatter"
                + " wraps it onto the next line}.");
    softly
        .assertThat(definitions)
        .as("links to a nested type, emitted elsewhere than the link points, get no label")
        .contains("Nested: {@link probe.Target.Inner}, {@link probe.Target.Inner.go}.");
    softly
        .assertThat(definitions)
        .as("links to a @TsIgnore type and its members get no label")
        .contains("Ignored: {@link probe.Ignored}, {@link probe.Ignored.member}.");
    softly
        .assertThat(definitions)
        .as("a link to a type outside this doclet run gets no label")
        .contains("Outside the run: {@link probe.Foo}.");
    softly
        .assertThat(definitions)
        .as("a link to a plain class with nothing exported gets no label")
        .contains(
            "Not exportable: {@link com.vertispan.tsdefs.tests.tsdocs.doclet.links.labels.Plain}.");
    softly
        .assertThat(definitions)
        .as("a link to a native type gets no label")
        .contains("Native: {@link Object}.");
    softly
        .assertThat(definitions)
        .as("a native @TsInterface is emitted, so its links are labeled")
        .contains("Native TsInterface: {@link probe.Shape | Shape}.");
    softly
        .assertThat(definitions)
        .as("links to types and members the doclet does not emit get no label")
        .contains(
            "Not emitted: {@link java.lang.String}, {@link java.util.List.size}, {@link Promise},"
                + " {@link probe.Target.hidden}.");
    softly
        .assertThat(declaration(definitions, "class Target"))
        .as("unlabeled links within Target keep the type name")
        .contains("{@link probe.Target.go | Target.go()}")
        .contains("{@link probe.Target.BAR | Target.BAR}");
    softly
        .assertThat(declaration(definitions, "class SeeSource"))
        .as("@see tags in SeeSource label links the same way")
        .contains("@see {@link probe.Target.go | Target.go()}")
        .contains("@see {@link probe.Target.BAR | Target.BAR}")
        .contains("@see {@link probe.Target | Target}")
        .contains("@see {@link probe.Target.go | the starter}")
        .contains("@see \"a quoted string\"")
        .contains("@see <a href=\"https://example.com\">an html link</a>")
        .contains("@see Missing#thing the missing label")
        .contains(
            "@see {@link probe.Target.go | a see label that is long enough that the formatter"
                + " has to wrap it onto the next line}");
    softly.assertAll();
  }

  @Test
  public void testInterfaceOverrides() throws IOException {
    testDocs("overrides");

    String definitions =
        Files.readString(
            Paths.get("target/test-classes/overrides/types.d.ts"), StandardCharsets.UTF_8);

    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(definitions).as("covariant override in Qux").contains("get():Bar;");
    softly
        .assertThat(definitions)
        .as("javadoc of same-signature override in Bar")
        .contains("Returns the name, as documented on the sub-interface.");
    softly
        .assertThat(definitions)
        .as("undocumented same-signature override in Plain is not redeclared")
        .contains("\texport interface Plain extends Foo {\n\t\tplain():string;\n\t}");
    softly
        .assertThat(definitions)
        .as("override under a blank comment in BlankDoc is not redeclared")
        .contains("\texport interface BlankDoc extends Foo {\n\t\tblank():string;\n\t}");
    softly
        .assertThat(definitions)
        .as("override under an {@inheritDoc}-only comment in InheritDocOnly is not redeclared")
        .contains("\texport interface InheritDocOnly extends Foo {\n\t\tinherit():string;\n\t}");
    // An undocumented override copies the inherited docs, which typedoc keeps with @deprecated.
    softly
        .assertThat(declaration(definitions, "interface DeprecatedNoDoc"))
        .as("undocumented deprecated override in DeprecatedNoDoc")
        .contains("Returns the name, as declared on the super-interface.")
        .contains("@deprecated")
        .doesNotContain("@inheritDoc");
    softly
        .assertThat(declaration(definitions, "abstract class NamedImpl"))
        .as("undocumented class override in NamedImpl")
        .contains("Returns the name, as declared on the super-interface.")
        .doesNotContain("@inheritDoc");
    // As javadoc does: the superclass's docs before an interface's.
    softly
        .assertThat(declaration(definitions, "abstract class NamedMulti"))
        .as("override of a superclass and an interface method in NamedMulti")
        .contains("Returns the name, as documented on the superclass.")
        .doesNotContain("as declared on the super-interface");
    softly
        .assertThat(declaration(definitions, "interface Qux"))
        .as("override of an undocumented method in Qux has nothing to inherit")
        .doesNotContain("@inheritDoc");
    softly
        .assertThat(declaration(definitions, "abstract class RunnableImpl"))
        .as("override of a library method in RunnableImpl, whose docs can't be read")
        .contains("@inheritDoc");
    softly.assertAll();
  }

  @Test
  public void testExplicitUnions() throws IOException {
    testDocs("union");

    String definitions =
        Files.readString(Paths.get("target/test-classes/union/types.d.ts"), StandardCharsets.UTF_8);

    assertThat(definitions)
        .contains(
            "/**\n* A named union containing a number or an array of nullable numbers.\n*/\ntype ExplicitParamUnion = number|Array<number|null|undefined>;")
        .contains(
            "/**\n* A generic named union containing a number or an array of its type parameter.\n*/\ntype ExplicitUnion<T> = number|Array<T>|\"foo\";")
        .contains(
            "\t/**\n\t* A named union emitted inside a TypeScript namespace.\n\t*/\n\ttype NamespacedExplicitUnion = string|boolean;")
        .contains("getExplicitUnion():ExplicitParamUnion|null|undefined;")
        .contains(
            "useExplicitUnion(value:ExplicitUnion<ExplicitParamUnion>):ExplicitUnion<ExplicitParamUnion>;");

    // A union exists in TypeScript only at its @TsUnion name; its JsType name is for Java.
    SoftAssertions softly = new SoftAssertions();
    softly
        .assertThat(definitions)
        .as("a link to a named union in a namespace targets the TS alias")
        .contains(
            "Namespaced: {@link com.vertispan.tsdefs.tests.tsdocs.doclet.union.NamespacedExplicitUnion"
                + " | NamespacedExplicitUnion}.");
    softly
        .assertThat(definitions)
        .as("a link to a named union in the global namespace targets the TS alias")
        .contains("Global: {@link ExplicitParamUnion | ExplicitParamUnion}.");
    softly
        .assertThat(definitions)
        .as("a link to a named union ignores its JsType name")
        .contains(
            "Named JsType: {@link com.vertispan.tsdefs.tests.tsdocs.doclet.union.StringNamedUnion"
                + " | StringNamedUnion}.");
    softly
        .assertThat(definitions)
        .as("a link to an anonymous union, which has no declaration, gets no label")
        .contains(
            "Anonymous: {@link com.vertispan.tsdefs.tests.tsdocs.doclet.union.AnonymousUnion}.");
    softly.assertAll();
  }

  @Test
  public void testDefaultTypeArguments() throws IOException {
    testDocs("defaults");

    String definitions =
        Files.readString(
            Paths.get("target/test-classes/defaults/types.d.ts"), StandardCharsets.UTF_8);
    assertThat(definitions)
        .contains("interface DefaultedApi<T extends DefaultedItem = DefaultedItem>")
        .contains("read<V = any>(name:string):V;");
  }
}
