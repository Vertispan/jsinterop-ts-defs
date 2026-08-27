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
import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;
import org.junit.Test;

public class DocletTest {

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
  public void testExplicitUnions() throws IOException {
    testDocs("union");

    String definitions =
        Files.readString(Paths.get("target/test-classes/union/types.d.ts"), StandardCharsets.UTF_8);

    assertThat(definitions)
        .contains(
            "/**\n* A named union containing a number or an array of nullable numbers.\n*/\ntype ExplicitParamUnion = number|Array<number|null|undefined>;")
        .contains(
            "/**\n* A generic named union containing a number or an array of its type parameter.\n*/\ntype ExplicitUnion<T> = number|Array<T>;")
        .contains(
            "\t/**\n\t* A named union emitted inside a TypeScript namespace.\n\t*/\n\ttype NamespacedExplicitUnion = string|boolean;")
        .contains("getExplicitUnion():ExplicitParamUnion|null|undefined;")
        .contains(
            "useExplicitUnion(value:ExplicitUnion<ExplicitParamUnion>):ExplicitUnion<ExplicitParamUnion>;");
  }
}
