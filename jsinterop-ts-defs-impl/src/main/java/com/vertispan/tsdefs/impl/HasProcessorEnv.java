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
package com.vertispan.tsdefs.impl;

import com.vertispan.tsdefs.impl.model.TsDoc;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public interface HasProcessorEnv {

  Types types();

  Elements elements();

  LogWrapper messager();

  TsDoc getDocs(Element element);

  /**
   * Whether {@code message} on {@code element} is reported for the first time; false for every
   * later call with the same pair, so a diagnostic is reported once however often the element is
   * emitted, and different diagnostics on one element are each reported. The default reports every
   * time.
   */
  default boolean firstReport(Element element, String message) {
    return true;
  }

  /**
   * {@code env} with its diagnostics discarded, for conversions made only to compare results.
   * Claims no reports, so the conversion that emits still reports.
   */
  static HasProcessorEnv quiet(HasProcessorEnv env) {
    LogWrapper discard =
        new LogWrapper() {
          @Override
          public void printMessage(Diagnostic.Kind kind, CharSequence msg) {}

          @Override
          public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e) {}

          @Override
          public void printMessage(
              Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a) {}
        };
    return new HasProcessorEnv() {
      @Override
      public Types types() {
        return env.types();
      }

      @Override
      public Elements elements() {
        return env.elements();
      }

      @Override
      public LogWrapper messager() {
        return discard;
      }

      @Override
      public TsDoc getDocs(Element element) {
        return env.getDocs(element);
      }
    };
  }
}
