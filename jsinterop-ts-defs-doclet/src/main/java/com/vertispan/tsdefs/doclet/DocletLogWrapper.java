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
package com.vertispan.tsdefs.doclet;

import com.vertispan.tsdefs.impl.LogWrapper;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import jdk.javadoc.doclet.Reporter;

public class DocletLogWrapper implements LogWrapper {

  private final Reporter reporter;

  public DocletLogWrapper(Reporter reporter) {
    this.reporter = reporter;
  }

  @Override
  public void printMessage(Diagnostic.Kind kind, CharSequence msg) {
    reporter.print(kind, String.valueOf(msg));
  }

  @Override
  public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e) {
    reporter.print(kind, e, String.valueOf(msg));
  }

  @Override
  public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a) {
    reporter.print(kind, e, String.valueOf(msg));
  }
}
