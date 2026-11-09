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
    return matcher.find() ? matcher.group() : "";
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
