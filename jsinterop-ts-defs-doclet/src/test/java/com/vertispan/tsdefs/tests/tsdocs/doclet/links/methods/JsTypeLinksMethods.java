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
package com.vertispan.tsdefs.tests.tsdocs.doclet.links.methods;

import com.vertispan.tsdefs.tests.tsdocs.doclet.links.methods.nested.JsTypeC;
import com.vertispan.tsdefs.tests.tsdocs.doclet.links.methods.nested.JsTypeD;
import com.vertispan.tsdefs.tests.tsdocs.doclet.links.methods.nested.JsTypeE;
import com.vertispan.tsdefs.tests.tsdocs.doclet.links.methods.nested.JsTypeF;
import jsinterop.annotations.JsType;

/**
 * Testing link a method in same type {@link #doSomethingA()}
 *
 * <p>Testing link a method in same type {@link #doSomethingA2()}
 *
 * <p>Testing link a method in type in same package {@link JsTypeB#doSomethingB()}
 *
 * <p>Testing link a method in type in different package {@link JsTypeC#doSomethingC()}
 *
 * <p>Testing link a method in type with js name and different package {@link
 * JsTypeD#doSomethingD()}
 *
 * <p>Testing link a method in type with js name and different package with different method js name
 * {@link JsTypeE#doSomethingE()}
 *
 * <p>Testing link a method in type with js name and different package with different method js name
 * and namespace {@link JsTypeE#doSomethingE2()}
 *
 * <p>Testing link a method in type with js name and different package with different method js name
 * and namespace {@link JsTypeF#doSomethingF()}
 *
 * <p>Testing link a method in type with js name and different package with different method js name
 * and namespace {@link JsTypeF#doSomethingF2()}
 *
 * @see #doSomethingA()
 * @see #doSomethingA2()
 * @see JsTypeB#doSomethingB()
 * @see JsTypeC#doSomethingC()
 * @see JsTypeD#doSomethingD()
 * @see JsTypeE#doSomethingE()
 * @see JsTypeE#doSomethingE2()
 * @see JsTypeF#doSomethingF()
 * @see JsTypeF#doSomethingF2()
 */
@JsType
public class JsTypeLinksMethods {
  public void doSomethingA() {}

  public void doSomethingA2() {}
}
