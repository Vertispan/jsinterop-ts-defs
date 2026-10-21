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
import com.vertispan.tsdefs.impl.builders.HasProperties;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsProperty;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class FieldVisitor<T> extends TsElement {
  public FieldVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  /** Visits a field inherited by {@code inheritedBy}. */
  public FieldVisitor(Element element, TypeElement inheritedBy, HasProcessorEnv env) {
    super(element, inheritedBy, env);
  }

  public void visit(HasProperties<T> parent) {
    if (isField() && isExportable()) {
      parent.addProperty(
          TsProperty.builder(getName(), getType())
              .addModifiers(getJsModifiers())
              .setDocs(getDocs())
              .setDeprecated(isDeprecated())
              .setOptional(isJsNullable())
              .build());
    }
  }
}
