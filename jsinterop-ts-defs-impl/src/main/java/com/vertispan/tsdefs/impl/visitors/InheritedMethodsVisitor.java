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
package com.vertispan.tsdefs.impl.visitors;

import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.HasMembers;
import com.vertispan.tsdefs.impl.builders.TsElement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class InheritedMethodsVisitor<T> extends TsElement {

  public InheritedMethodsVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(HasMembers<T> parent) {
    // Listed once per path to it; emitted once.
    Set<ExecutableElement> nonOverriddenMethods =
        new LinkedHashSet<>(allNotOverriddenInterfacesMethods());
    // Only the most specific override is emitted: a wider method from further up would be a second
    // overload, and callers may get its wider type.
    Set<ExecutableElement> overriders = new LinkedHashSet<>(nonOverriddenMethods);
    overriders.addAll(mergedSuperClassMethods());
    List<ExecutableElement> copied =
        nonOverriddenMethods.stream()
            .filter(method -> !isOverriddenByAny(method, overriders))
            .collect(Collectors.toList());
    // An override that only makes the return type nullable can't be an overload next to the method
    // it overrides (TsType equality ignores nullability), so that one is copied alone, as the
    // emitted supertypes declare it.
    copied.stream()
        .filter(method -> !TsElement.of(method, env).onlyAddsNullabilityTo(copied))
        .forEach(
            executableElement -> {
              new ClassMethodVisitor<T>(executableElement, (TypeElement) element, false, env)
                  .visit(parent);
            });
  }
}
