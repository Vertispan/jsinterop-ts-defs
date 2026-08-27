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
package com.vertispan.tsdefs.impl.visitors;

import com.vertispan.tsdefs.impl.HasProcessorEnv;
import com.vertispan.tsdefs.impl.builders.JavaToTsTypeConverter;
import com.vertispan.tsdefs.impl.builders.TsElement;
import com.vertispan.tsdefs.impl.model.TypeScriptModule;
import javax.lang.model.element.Element;

public class TsExplicitUnionTypeVisitor extends TsElement {

  public TsExplicitUnionTypeVisitor(Element element, HasProcessorEnv env) {
    super(element, env);
  }

  public void visit(TypeScriptModule.TsModuleBuilder moduleBuilder) {
    if (isPublic() && isExplicitUnionType()) {
      JavaToTsTypeConverter converter = new JavaToTsTypeConverter(element, env);
      moduleBuilder.addTsType(converter.unionType(element), getDocs());
    }
  }
}
