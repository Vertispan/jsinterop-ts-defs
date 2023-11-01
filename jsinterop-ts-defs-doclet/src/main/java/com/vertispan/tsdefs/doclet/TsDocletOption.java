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

import java.util.List;
import java.util.function.BiPredicate;
import jdk.javadoc.doclet.Doclet;

public abstract class TsDocletOption implements Doclet.Option {
  public static TsDocletOption of(
      String name, String description, BiPredicate<String, List<String>> process) {
    return new TsDocletOption() {

      @Override
      public String getDescription() {
        return description;
      }

      @Override
      public List<String> getNames() {
        return List.of(name);
      }

      @Override
      public boolean process(String s, List<String> list) {
        return process.test(s, list);
      }
    };
  }

  public static TsDocletOption ignored(String name) {
    return of(name, "ignored, only supported to appease Gradle", (opt, args) -> true);
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public String getParameters() {
    return "value";
  }
}
