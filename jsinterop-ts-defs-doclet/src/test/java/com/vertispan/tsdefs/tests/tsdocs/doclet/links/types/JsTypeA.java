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
package com.vertispan.tsdefs.tests.tsdocs.doclet.links.types;

import com.vertispan.tsdefs.tests.tsdocs.doclet.links.types.nested.JsTypeF;
import com.vertispan.tsdefs.tests.tsdocs.doclet.links.types.nested.JsTypeG;
import com.vertispan.tsdefs.tests.tsdocs.doclet.links.types.nested.JsTypeH;
import com.vertispan.tsdefs.tests.tsdocs.doclet.links.types.nested.JsTypeI;
import jsinterop.annotations.JsType;

/**
 * The idea of this JsType is to test transforming java docs into Ts docs.
 *
 * <p>{@link JsTypeD}
 *
 * <p>Testing a link to another class in same package {@link JsTypeD}
 *
 * <p>Testing a link to another class in same package with label {@link JsTypeD type D in same
 * package}
 *
 * <p>Testing a link to another class in same package with Js name {@link JsTypeB}
 *
 * <p>Testing a link to another class in same package with label and Js name {@link JsTypeB type B
 * in same package with js name}
 *
 * <p>Testing a link to another class in same package with Js namespace {@link JsTypeC}
 *
 * <p>Testing a link to another class in same package with label and Js namespace {@link JsTypeC
 * type C in same package with js namespace}
 *
 * <p>Testing a link to another class in same package with Js name and namespace {@link JsTypeE}
 *
 * <p>Testing a link to another class in same package with label and Js name and namespace {@link
 * JsTypeE type E in same package with js name and namespace}
 *
 * <p>Testing a link to another class in different package {@link JsTypeF}
 *
 * <p>Testing a link to another class in different package with label {@link JsTypeF type F in
 * different package}
 *
 * <p>Testing a link to another class in different package with js name {@link JsTypeG}
 *
 * <p>Testing a link to another class in same package with js name with label {@link JsTypeG type G
 * with js name}
 *
 * <p>Testing a link to another class in different package with js namespace {@link JsTypeH}
 *
 * <p>Testing a link to another class in same package with js namespace with label {@link JsTypeH
 * type H with js name}
 *
 * <p>Testing a link to another class in different package with js name and namespace {@link
 * JsTypeI}
 *
 * <p>Testing a link to another class in same package with js name and namespace with label {@link
 * JsTypeI type I with js name and namespace}
 *
 * <p>Testing a link to none existing type {@link JsTypeNotExist}
 *
 * <p>Testing a link with generics {@link JsTypeJ<JsTypeD>}
 *
 * <p>Testing a link to external {@link <a href="https://typedoc.org/guides/tags/">tags</a>}
 *
 * <p>this is some inlined code {@code public void doSomething(){}}
 *
 * <p>while this is some code with multi-lines must start in a new line {@code public void
 * doSomething(){ DomGlobal.console.info("hello); } }
 *
 * <p>hello
 *
 * @see <a href="https://typedoc.org/guides/tags/">tags</a>
 * @see <a href="https://typedoc.org/guides/tags/">tags</a>
 * @see JsTypeI
 */
@JsType
public class JsTypeA {

  public void doSomethingA() {}
}
