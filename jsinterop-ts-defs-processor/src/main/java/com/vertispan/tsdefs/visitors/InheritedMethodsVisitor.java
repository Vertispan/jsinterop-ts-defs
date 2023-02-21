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
package com.vertispan.tsdefs.visitors;

import com.vertispan.tsdefs.HasProcessorEnv;
import com.vertispan.tsdefs.builders.HasMembers;
import com.vertispan.tsdefs.builders.TsElement;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class InheritedMethodsVisitor<T> extends TsElement {

  public InheritedMethodsVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(HasMembers<T> parent) {
    List<ExecutableElement> nonOverriddenMethods = allNotOverriddenInterfacesMethods();
    nonOverriddenMethods.forEach(
        executableElement -> {
          new ClassMethodVisitor<T>(executableElement, env).visit(parent);
        });
  }
}
