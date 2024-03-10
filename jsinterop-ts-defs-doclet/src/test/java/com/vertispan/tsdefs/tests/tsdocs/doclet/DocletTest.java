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
package com.vertispan.tsdefs.tests.tsdocs.doclet;

import com.vertispan.tsdefs.doclet.TsDoclet;
import java.io.IOException;
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
  public void brandedTypes() throws IOException {
    testDocs("branded");
  }
}
