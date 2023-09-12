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
package com.vertispan.tsdefs.tests.types;

import java.util.List;
import java.util.Map;
import java.util.Set;
import jsinterop.annotations.*;

/*
1- Handle setters and getters as properties// needs confirmation.
2- generate JsFunction as Ts function.
3- Generate enums as TypeDefs constants.
4- JsMap vs java map / handle imports and error out when not found, see #7.
5- Count for name spaces.
6- Add tests.
7- Boxed primitives except Double, Boolean are just unknown(with annotation)/error.
8- Having a public field and public accessor should error out unless ignored.
9- Export in regard to access level when the field/method is annotated as JsProperty/JsMethod.
10- Should error out for multiple constructors or members with colliding names.

 */

/**
 * This is a sample class to test all kind of cases for exporting JsInterop to TS Type definition
 * This is another line to see how is this being processed by the annotation processor
 *
 * @return String testing return tag
 * @see {@link FooInterface}
 */
@JsType
public class Client implements FooInterface {

  public static final String ACONSTANT = "HELLO WORLD";

  @JsProperty(name = "initialName")
  public String firstName;

  @JsProperty public final String lastName;
  @JsProperty public float pFloatF;
  public int intField;
  public Float floatF;
  public Double doubleF;
  public double doublePf;
  public Boolean booleanF;
  public boolean booleanPf;
  public Void voidF;
  @JsIgnore public Integer ignoredField;

  @JsProperty private String privateButExported;

  public ChildNoTypeArgs childNoTypeArgs;
  public ChildSingleTypeArg<ChildNoTypeArgs> childSingleTypeArg;
  public ChildMultiTypeArgs<ChildNoTypeArgs, ChildSingleTypeArg<String>, Integer, Boolean>
      childMultiTypeArgs;

  public String[] addresses;
  public String[][] array2d;

  public List<ChildNoTypeArgs> childNoTypeArgsList;
  public List<ChildSingleTypeArg<ChildNoTypeArgs>> childSingleTypeArgList;

  public Set<ChildNoTypeArgs> setField;

  public Map<String, Integer> mapField;

  private String shouldNotBeExported;

  private String shouldExportSetterAndGetter;
  private String shouldExportSetterAndGetter2;
  private Integer shouldExportSetter;
  private Integer shouldExportGetter;

  // Should not be exported
  private Client() {
    this.lastName = "";
  }

  public Client(String lastName) {
    this.lastName = lastName;
  }

  /**
   * This is a sample class to test all kind of cases for exporting JsInterop to TS Type definition
   * This is another line to see how is this being processed by the annotation processor
   *
   * @return String testing return tag
   * @see {@link FooInterface}
   */
  @JsConstructor
  public Client(String lastName, String[] addresses) {
    this.lastName = lastName;
    this.addresses = addresses;
  }

  public Client(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  /**
   * This is a sample class to test all kind of cases for exporting JsInterop to TS Type definition
   * This is another line to see how is this being processed by the annotation processor
   *
   * @param arg just testing params
   * @return String testing return tag
   * @see {@link FooInterface}
   */
  @JsMethod(namespace = "com.vertispan.diffns.OtherClient")
  public static String differentNs(int arg) {
    return null;
  }

  @Deprecated
  public void doSomething() {}

  /**
   * This is a sample class to test all kind of cases for exporting JsInterop to TS Type definition
   * This is another line to see how is this being processed by the annotation processor
   *
   * @param name just testing params
   * @return String testing return tag
   * @see {@link FooInterface}
   * @deprecated
   */
  @Deprecated
  public void doSomethingWithArgs(String name, Double id) {}

  public String doSomethingWithArgsAndReturnType(String name, int index) {
    return "";
  }

  @JsProperty
  public String getShouldExportSetterAndGetter() {
    return shouldExportSetterAndGetter;
  }

  @JsProperty
  public void setShouldExportSetterAndGetter(String shouldExportSetterAndGetter) {
    this.shouldExportSetterAndGetter = shouldExportSetterAndGetter;
  }

  @JsProperty(name = "alterGetterName")
  public String getShouldExportSetterAndGetter2() {
    return shouldExportSetterAndGetter2;
  }

  @JsProperty(name = "alterGetterName")
  public void setShouldExportSetterAndGetter2(String shouldExportSetterAndGetter2) {
    this.shouldExportSetterAndGetter2 = shouldExportSetterAndGetter2;
  }

  @JsProperty
  public void setShouldExportSetter(Integer shouldExportSetter) {
    this.shouldExportSetter = shouldExportSetter;
  }

  public Integer getShouldExportGetter() {
    return shouldExportGetter;
  }

  @JsMethod
  private String privateMethodBurExported() {
    return "";
  }

  public String getSomeValue() {
    return null;
  }

  @Override
  public void setSomeValue(String value) {}

  public void takesFunction(FunctionArgs<String, Double> func) {}

  @Override
  public String shouldBeExportedInInheritors() {
    return null;
  }
}
