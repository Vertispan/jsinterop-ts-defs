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

import static java.util.Objects.nonNull;

import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.*;
import javax.lang.model.element.Element;

public class TsTypeDefVisitor extends TsElement {

  public TsTypeDefVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(TypeScriptModule.TsModuleBuilder moduleBuilder) {
    if (isClass() && isPublic() && isTsTypeDef()) {

      TsTypeDef.TsEnumBuilder builder =
          TsTypeDef.builder(getName(), getNamespace(), (TsCustomType) getType())
              .setDocs(getDocs())
              .setDeprecated(isDeprecated())
              .addModifiers(TsModifier.EXPORT);

      element.getEnclosedElements().stream()
          .map(e -> TsElement.of(e, env))
          .forEach(
              e -> {
                if (e.isStatic() && e.isPublic() && e.isField() && e.isFinal()) {
                  builder.addEnumeration(e.getName());
                } else {
                  new ClassMethodVisitor<TsTypeDef.TsEnumBuilder>(e.element(), env).visit(builder);
                }
              });

      moduleBuilder.addTsTypeDef(builder.build());
    }
  }

  public TsType getType() {
    com.vertispan.tsdefs.annotations.TsTypeDef tsTypeDef =
        element.getAnnotation(com.vertispan.tsdefs.annotations.TsTypeDef.class);
    if (nonNull(tsTypeDef.name())
        && !tsTypeDef.name().trim().isEmpty()
        && !"<auto>".equals(tsTypeDef.name())) {
      return TsCustomType.of(tsTypeDef.name(), getNamespace(), TsType.of(tsTypeDef.tsType()));
    }
    return TsCustomType.of(getName() + "Type", getNamespace(), TsType.of(tsTypeDef.tsType()));
  }
}
