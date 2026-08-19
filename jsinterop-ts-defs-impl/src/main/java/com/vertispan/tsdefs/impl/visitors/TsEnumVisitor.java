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
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TsBrandedType;
import com.vertispan.tsdefs.impl.model.TsModifier;
import com.vertispan.tsdefs.impl.model.TsType;
import com.vertispan.tsdefs.impl.model.TypeScriptModule;
import javax.lang.model.element.Element;

public class TsEnumVisitor extends TsElement {

  public TsEnumVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(TypeScriptModule.TsModuleBuilder moduleBuilder) {
    if (isEnum() && isPublic()) {

      String name = getName();
      String namespace = getNamespace();
      TsType type = getType();
      TsBrandedType.TsBrandedTypeBuilder builder =
          TsBrandedType.builder(name, namespace, type)
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
                  new ClassMethodVisitor<TsBrandedType.TsBrandedTypeBuilder>(e.element(), env)
                      .visit(builder);
                }
              });

      moduleBuilder.addBrandedType(builder.build());
    }
  }
}
