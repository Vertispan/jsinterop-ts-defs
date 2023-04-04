// Minimum TypeScript Version: 4.3
import {com} from "./index";

// --- JsTypes imports -----------------------------------
// --- Classes that are not JS types should not be exported ---
// @ts-expect-error
import NoJsType = com.vertispan.tsdefs.properties.NoJsType;


// --- JsTypes with no members should not be exported. ---
// @ts-expect-error
import JSTypeWithNoMembers = com.vertispan.tsdefs.properties.JSTypeWithNoMembers;

// --- All members exported to a different name and namespace
// @ts-expect-error
import JsTypeWithPropertiesAndNameAndNameSpace = com.vertispan.tsdefs.properties.JsTypeWithPropertiesAndNameAndNameSpace;

import DifferentJsTypeWithPropertiesAndNameAndNameSpace = com.vertispan.differentNameSpace.DifferentJsTypeWithPropertiesAndNameAndNameSpace;
import OtherType = com.vertispan.differentNameSpace.OtherType;

// --- JsTypes properties should be exported with the correct type
import JsTypeWithProperties = com.vertispan.tsdefs.properties.JsTypeWithProperties;
import OtherNameSpace = com.OtherNameSpace;
import NonJsTypeWithExportableProperties = com.vertispan.tsdefs.properties.NonJsTypeWithExportableProperties;

// ------------ JsFunctions ------------------
import JsFunctionsClient = com.vertispan.tsdefs.jsfunctions.JsFunctionsClient;

// --- Constructors imports ---------------------

import JsTypeWithConstructor = com.vertispan.tsdefs.constructors.JsTypeWithConstructor;
import JsTypeWithJsIgnoredAndNonIgnoredConstructor = com.vertispan.tsdefs.constructors.JsTypeWithJsIgnoredAndNonIgnoredConstructor;
import JsTypeWithJsIgnoredConstructor = com.vertispan.tsdefs.constructors.JsTypeWithJsIgnoredConstructor;
import JsTypeWithJsIgnoredConstructorAndDefaultConstructor = com.vertispan.tsdefs.constructors.JsTypeWithJsIgnoredConstructorAndDefaultConstructor;
import JsTypeWithJsIgnoredConstructorAndJsIgnoredDefaultConstructor = com.vertispan.tsdefs.constructors.JsTypeWithJsIgnoredConstructorAndJsIgnoredDefaultConstructor;
import JsTypeWithNoConstructors = com.vertispan.tsdefs.constructors.JsTypeWithNoConstructors;
import NonJsTypeWithJsConstructor = com.vertispan.tsdefs.constructors.NonJsTypeWithJsConstructor;
import NonJsTypeWithJsIgnoredAndNonIgnoredConstructor = com.vertispan.tsdefs.constructors.NonJsTypeWithJsIgnoredAndNonIgnoredConstructor;
import NonJsTypeWithJsIgnoredConstructor = com.vertispan.tsdefs.constructors.NonJsTypeWithJsIgnoredConstructor;

import JsTypeWithMethods = com.vertispan.tsdefs.methods.JsTypeWithMethods;
import NonJsTypeWithMethods = com.vertispan.tsdefs.methods.NonJsTypeWithMethods;

import JsTypeWithSettersAndGetters = com.vertispan.tsdefs.settergetter.JsTypeWithSettersAndGetters;
import JsTypeInterface = com.vertispan.tsdefs.interfaces.JsTypeInterface;
import JsTypeChild = com.vertispan.tsdefs.inheritance.JsTypeChild;
import JsInterfaceByTsName = com.vertispan.tsdefs.tsname.JsInterfaceByTsName;
// @ts-expect-error
import JsInterfaceWithTsName = com.vertispan.tsdefs.tsname.JsInterfaceWithTsName;
// @ts-expect-error
import JsInterfaceByTsName2 = com.vertispan.tsnamesspace.JsInterfaceByTsName;

import JsTypeByTsNameAndNamespaceInsteadOfJsName = com.vertispan.tsnamesspace.JsTypeByTsNameAndNamespaceInsteadOfJsName;
// @ts-expect-error
import JsTypeByJsNameAndNamespace = com.vertispan.jsnamesspace.JsTypeByTsNameAndNamespaceInsteadOfJsName;
// @ts-expect-error
import JsTypeWithJsAndTsTsNameAndTsNamespace = com.vertispan.tsdefs.tsname.JsTypeWithJsAndTsTsNameAndTsNamespace;

// @ts-expect-error
import JsTypeWithJsNameAndTsName = com.vertispan.tsdefs.tsname.JsTypeWithJsNameAndTsName;
// @ts-expect-error
import JsTypeByJsName = com.vertispan.tsdefs.tsname.JsTypeByJsName;
import JsTypeByTsNameInsteadOfJsName = com.vertispan.tsdefs.tsname.JsTypeByTsNameInsteadOfJsName;
// @ts-expect-error
import JsTypeWithTsName = com.vertispan.tsdefs.tsname.JsTypeWithTsName;
import JsTypeByTsName = com.vertispan.tsdefs.tsname.JsTypeByTsName;

import JsTypeWithDataTypes = com.vertispan.tsdefs.datatypes.JsTypeWithDataTypes;
import GenericJsType = com.vertispan.tsdefs.generics.GenericJsType;
import GenericJsTypeExtendsGenericJsTye = com.vertispan.tsdefs.generics.GenericJsTypeExtendsGenericJsTye;
import JsTypeExtendsAbstractGenericJsType = com.vertispan.tsdefs.generics.JsTypeExtendsAbstractGenericJsType;
import JsTypeImplementingGenericInterfaces = com.vertispan.tsdefs.generics.JsTypeImplementingGenericInterfaces;
import NonGenericJsTypeExtendsGenericJsType = com.vertispan.tsdefs.generics.NonGenericJsTypeExtendsGenericJsType;
import DualGenericsJsType = com.vertispan.tsdefs.generics.DualGenericsJsType;

import UsingInterfaceWithMethodTypeArgs = com.vertispan.tsdefs.generics.UsingInterfaceWithMethodTypeArgs;
import InterfaceWithGenericMethod = com.vertispan.tsdefs.generics.InterfaceWithGenericMethod;
import GenericInterfaceWithGenericMethod = com.vertispan.tsdefs.generics.GenericInterfaceWithGenericMethod;
import GenericInterfaceWithDualGenericMethod = com.vertispan.tsdefs.generics.GenericInterfaceWithDualGenericMethod;

import EnumSimulation = com.vertispan.tsdefs.tstypedef.EnumSimulation;
import EnumSimulationType = com.vertispan.tsdefs.tstypedef.EnumSimulationType;
import EnumClient = com.vertispan.tsdefs.tstypedef.EnumClient;
import UsedAsTypeRef = com.vertispan.tsdefs.tstypedef.UsedAsTypeRef;

import DayOfWeek = com.vertispan.calendar.DayOfWeek
import ItemDetails = com.vertispan.storage.ItemDetails;
import ItemType = com.vertispan.storage.ItemType;
import ItemTypeType = com.vertispan.storage.ItemTypeType;

// @ts-expect-error
import TsIgnoredSuperClass = com.vertispan.tsdefs.inheritance.TsIgnoredSuperClass;
import JsTypeExtendsTsIgnoredSuperType = com.vertispan.tsdefs.inheritance.JsTypeExtendsTsIgnoredSuperType;
import JsTypeWithJsNullableMembers = com.vertispan.tsdefs.jsnullable.JsTypeWithJsNullableMembers;

import TypeExtendingTsInterfaceType = com.vertispan.tsdefs.tsinterface.TypeExtendingTsInterfaceType;
import JsTypeAsTsInterface = com.vertispan.tsdefs.tsinterface.JsTypeAsTsInterface;

import JsTypeWithStaticMethods = com.vertispan.tsdefs.methods.JsTypeWithStaticMethods;

// ---------- JsNullable ---------------

import JsInterfaceWithJsNullableSetGet = com.vertispan.tsdefs.jsnullable.JsInterfaceWithJsNullableSetGet
import ExtendsInterfaceWithJsNullableProperty = com.vertispan.tsdefs.jsnullable.ExtendsInterfaceWithJsNullableProperty

// ---------------- Union Types ----------------

import UnionTypeApi = com.vertispan.tsdefs.tsunion.UnionTypeApi;

// ---------- Properties tests -------------------------
const jsTypeWithProperties = new JsTypeWithProperties();

const fromOtherNameSpace = new OtherNameSpace();

// $ExpectType number
jsTypeWithProperties.intProperty;
// $ExpectType unknown
jsTypeWithProperties.integerProperty;
// $ExpectType number
jsTypeWithProperties.doubleProperty;
// $ExpectType number
jsTypeWithProperties.doubleWrapperProperty;
// $ExpectType unknown
jsTypeWithProperties.floatProperty;
// $ExpectType unknown
jsTypeWithProperties.floatWrapperProperty;
// $ExpectType boolean
jsTypeWithProperties.booleanProperty;
// $ExpectType boolean
jsTypeWithProperties.booleanWrapperProperty;
// $ExpectType void
jsTypeWithProperties.voidType;
// $ExpectType string
jsTypeWithProperties.stringProperty;
// $ExpectType string
jsTypeWithProperties.differentName;
// $ExpectType OtherJsType
jsTypeWithProperties.otherJsTypeProperty;
// @ts-expect-error
jsTypeWithProperties.withDifferentName;
// @ts-expect-error
jsTypeWithProperties.privateStringProperty;

// --- Ignored properties should not be exported
// @ts-expect-error
jsTypeWithProperties.jsIgnoredProperty;

JsTypeWithProperties.GLOBAL_EDITABLE = "new value";

// @ts-expect-error
JsTypeWithProperties.GLOBAL_READONLY = "new value";

// --- Should be exported into com.OtherNameSpace ---
// @ts-expect-error
jsTypeWithProperties.propInOtherNamespace;

fromOtherNameSpace.propInOtherNamespace = "string";

const nonJsTypeWithExportableProperties = new NonJsTypeWithExportableProperties();

// @ts-expect-error
nonJsTypeWithExportableProperties.shouldNotBeExported = "";

nonJsTypeWithExportableProperties.shouldBeExported = "";

// @ts-expect-error
nonJsTypeWithExportableProperties.privateShouldNotBeExported = "";

nonJsTypeWithExportableProperties.privateButExported = "";

const differentJsTypeWithPropertiesAndNameAndNameSpace = new DifferentJsTypeWithPropertiesAndNameAndNameSpace();
differentJsTypeWithPropertiesAndNameAndNameSpace.property = "";
differentJsTypeWithPropertiesAndNameAndNameSpace.theThirdProperty = "";
differentJsTypeWithPropertiesAndNameAndNameSpace.fourthProperty = "";
// @ts-expect-error
differentJsTypeWithPropertiesAndNameAndNameSpace.theOtherProperty = "";

const otherType = new OtherType();
otherType.theOtherProperty = "";
// @ts-expect-error
otherType.property = "";
// @ts-expect-error
otherType.theThirdProperty = "";
// @ts-expect-error
otherType.fourthProperty = "";

// ---------- End Properties tests -------------------------

// ----------- Constructors tests ---------------------------

const jsTypeWithConstructor = new JsTypeWithConstructor("text");
// @ts-expect-error
const jsTypeWithConstructor2 = new JsTypeWithConstructor();

// @ts-expect-error
const jsTypeWithJsIgnoredAndNonIgnoredConstructor = new JsTypeWithJsIgnoredAndNonIgnoredConstructor();
// @ts-expect-error
const jsTypeWithJsIgnoredAndNonIgnoredConstructor_1 = new JsTypeWithJsIgnoredAndNonIgnoredConstructor("text")
const jsTypeWithJsIgnoredAndNonIgnoredConstructor_2 = new JsTypeWithJsIgnoredAndNonIgnoredConstructor("text", "anotherText");

// @ts-expect-error
const jsTypeWithJsIgnoredConstructor = new JsTypeWithJsIgnoredConstructor();
// @ts-expect-error
const jsTypeWithJsIgnoredConstructor_1 = new JsTypeWithJsIgnoredConstructor("text");

class JsTypeWithJsIgnoredConstructorChild_1 extends JsTypeWithJsIgnoredConstructor {

    constructor() {
        super();
    }
}
class JsTypeWithJsIgnoredConstructorChild_2 extends JsTypeWithJsIgnoredConstructor {

    constructor() {
        // @ts-expect-error
        super("text");
    }
}

// Test for https://github.com/Vertispan/jsinterop-ts-defs/issues/31
const jsTypeWithJsIgnoredConstructorAndDefaultConstructor = new JsTypeWithJsIgnoredConstructorAndDefaultConstructor();
// @ts-expect-error
const jsTypeWithJsIgnoredConstructorAndDefaultConstructor2 = new JsTypeWithJsIgnoredConstructorAndDefaultConstructor("text");

// @ts-expect-error
const jsTypeWithJsIgnoredConstructorAndJsIgnoredDefaultConstructor= new JsTypeWithJsIgnoredConstructorAndJsIgnoredDefaultConstructor();
// @ts-expect-error
const jsTypeWithJsIgnoredConstructorAndJsIgnoredDefaultConstructor= new JsTypeWithJsIgnoredConstructorAndJsIgnoredDefaultConstructor("text");

class jsTypeWithJsIgnoredConstructorAndJsIgnoredDefaultConstructor_child1 extends JsTypeWithJsIgnoredConstructorAndJsIgnoredDefaultConstructor {

    constructor() {
        super();
    }
}
class jsTypeWithJsIgnoredConstructorAndJsIgnoredDefaultConstructor_child2 extends JsTypeWithJsIgnoredConstructor {

    constructor() {
        // @ts-expect-error
        super("text");
    }
}

const jsTypeWithNoConstructors = new JsTypeWithNoConstructors();

// @ts-expect-error
const nonJsTypeWithJsConstructor = new NonJsTypeWithJsConstructor();

const nonJsTypeWithJsConstructor_1 = new NonJsTypeWithJsConstructor("text");

// @ts-expect-error
const nonJsTypeWithJsIgnoredAndNonIgnoredConstructor = new NonJsTypeWithJsIgnoredAndNonIgnoredConstructor();
// @ts-expect-error
const nonJsTypeWithJsIgnoredAndNonIgnoredConstructor_1 = new NonJsTypeWithJsIgnoredAndNonIgnoredConstructor("text");
const nonJsTypeWithJsIgnoredAndNonIgnoredConstructor_2 = new NonJsTypeWithJsIgnoredAndNonIgnoredConstructor("text", "otherText");

// @ts-expect-error
const nonJsTypeWithJsIgnoredConstructor = new NonJsTypeWithJsIgnoredConstructor();
// @ts-expect-error
const nonJsTypeWithJsIgnoredConstructor_1 = new NonJsTypeWithJsIgnoredConstructor("text");
// @ts-expect-error
const nonJsTypeWithJsIgnoredConstructor_1 = new NonJsTypeWithJsIgnoredConstructor("text", "anotherText");
// @ts-expect-error
const nonJsTypeWithJsIgnoredConstructor_1 = new NonJsTypeWithJsIgnoredConstructor("text", "anotherText", "thirdText");

class nonJsTypeWithJsIgnoredConstructor_child extends NonJsTypeWithJsIgnoredConstructor {

    constructor() {
        super();
    }
}
class nonJsTypeWithJsIgnoredConstructor_child1 extends NonJsTypeWithJsIgnoredConstructor {

    constructor() {
        // @ts-expect-error
        super("text");
    }
}
class nonJsTypeWithJsIgnoredConstructor_child2 extends NonJsTypeWithJsIgnoredConstructor {

    constructor() {
        // @ts-expect-error
        super("text", "anotherText");
    }
}
class nonJsTypeWithJsIgnoredConstructor_child3 extends NonJsTypeWithJsIgnoredConstructor {

    constructor() {
        // @ts-expect-error
        super("text", "anotherText", "thirdText");
    }
}

// -------------- Methods test -------------------------

const jsTypeWithMethods = new JsTypeWithMethods();

// $ExpectType void
jsTypeWithMethods.takesNothingReturnVoid();
// $ExpectType void
jsTypeWithMethods.takesNothingReturnVoidRenamed();
// @ts-expect-error
jsTypeWithMethods.takesNothingReturnVoidWithDifferentName();
// @$ExpectType string
jsTypeWithMethods.takesDoubleReturnString(1.0);
// @ts-expect-error
jsTypeWithMethods.takesDoubleReturnString("text");
// @ts-expect-error
jsTypeWithMethods.takesDoubleReturnString(true);
// @$ExpectType string
jsTypeWithMethods.takesMultipleParamsReturnString("text", 1.0, true);
// @ts-expect-error
jsTypeWithMethods.takesMultipleParamsReturnString(1.0, "text", true);
// @ts-expect-error
jsTypeWithMethods.takesMultipleParamsReturnString(true, "text", 1.0);
// @ts-expect-error
jsTypeWithMethods.protectedMethod();
// @$ExpectType string
jsTypeWithMethods.privateMethodButExported(1.0);
// @ts-expect-error
jsTypeWithMethods.privateMethodButNotExported(1.0);
jsTypeWithMethods.jsPropertyMethod;
jsTypeWithMethods.methodOne();
JsTypeWithMethods.methodOne();

class JsTypeWithMethodsChild extends JsTypeWithMethods {

    doNothing():void {
        super.protectedMethod();
        // @ts-expect-error
        super.protectedButNotExportedMethod();
    }
}

const otherTypeWithMethods = new OtherType();
otherTypeWithMethods.doSomething();
otherTypeWithMethods.doSomethingElse();

const nonJsTypeWithMethods = new NonJsTypeWithMethods();

nonJsTypeWithMethods.takesNothingReturnVoidExported();
// @ts-expect-error
nonJsTypeWithMethods.takesNothingReturnVoid();
nonJsTypeWithMethods.takesNothingReturnVoidRenamed();
// @ts-expect-error
nonJsTypeWithMethods.takesDoubleReturnString(1.0);
// @ts-expect-error
nonJsTypeWithMethods.takesMultipleParamsReturnString(1.0, "text", true);
// @ts-expect-error
nonJsTypeWithMethods.protectedMethod();
// @ts-expect-error
nonJsTypeWithMethods.protectedButNotExportedMethod();
nonJsTypeWithMethods.privateMethodButExported(1.0);
// @ts-expect-error
nonJsTypeWithMethods.privateMethodButNotExported(1.0);

class NonJsTypeWithMethodsChild extends NonJsTypeWithMethods {

    doNothing():void {
        super.protectedMethod();
        // @ts-expect-error
        super.protectedButNotExportedMethod();
    }
}

const jsTypeWithStaticMethods = new JsTypeWithStaticMethods();

jsTypeWithStaticMethods.doSomething();
JsTypeWithStaticMethods.doSomethingStatic();
JsTypeWithStaticMethods.publicStaticProperty;
// @ts-expect-error
JsTypeWithStaticMethods.privateStaticProperty;
// @ts-expect-error
JsTypeWithStaticMethods.privateStaticMethod();
JsTypeWithStaticMethods.privateStaticJsProperty;
JsTypeWithStaticMethods.privateStaticJsMethod();
// @ts-expect-error
JsTypeWithStaticMethods.publicStaticIgnoredProperty;
// @ts-expect-error
JsTypeWithStaticMethods.publicStaticIgnoredMethod();

// ------------- Setters and Getters ---------------------------

const jsTypeWithSettersAndGetters = new JsTypeWithSettersAndGetters("text");

jsTypeWithSettersAndGetters.getPropertyA();
jsTypeWithSettersAndGetters.setPropertyA("text");
// @ts-expect-error
const propertyAValue= jsTypeWithSettersAndGetters.propertyA;
// @ts-expect-error
jsTypeWithSettersAndGetters.propertyA = "text";

jsTypeWithSettersAndGetters.getPropertyB();
jsTypeWithSettersAndGetters.setPropertyB("text");
// @ts-expect-error
const propertyBValue= jsTypeWithSettersAndGetters.propertyB;
// @ts-expect-error
jsTypeWithSettersAndGetters.propertyB = "text";

const propertyCValue = jsTypeWithSettersAndGetters.propertyC;
jsTypeWithSettersAndGetters.propertyC = "text";
// @ts-expect-error
jsTypeWithSettersAndGetters.getPropertyC();
// @ts-expect-error
jsTypeWithSettersAndGetters.setPropertyC("text");

const propertyEValue_1= jsTypeWithSettersAndGetters.getPropertyE();
// @ts-expect-error
jsTypeWithSettersAndGetters.propertyE = "text";
// @ts-expect-error
const propertyEValue = jsTypeWithSettersAndGetters.propertyE;

jsTypeWithSettersAndGetters.setPropertyF("text");
// @ts-expect-error
jsTypeWithSettersAndGetters.propertyF = "text";
// @ts-expect-error
const propertyFValue = jsTypeWithSettersAndGetters.propertyF;

const propertyGValue =jsTypeWithSettersAndGetters.renamedPropertyG;
// @ts-expect-error
const propertyGValue2 =jsTypeWithSettersAndGetters.getRenamedPropertyG();
// @ts-expect-error
const propertyGValue3 =jsTypeWithSettersAndGetters.propertyG;
// @ts-expect-error
const propertyGValue4 =jsTypeWithSettersAndGetters.getPropertyG();
// @ts-expect-error
jsTypeWithSettersAndGetters.renamedPropertyG= "text";
// @ts-expect-error
jsTypeWithSettersAndGetters.propertyG= "text";
// @ts-expect-error
jsTypeWithSettersAndGetters.setRenamedPropertyG("text");
// @ts-expect-error
jsTypeWithSettersAndGetters.setPropertyG("text");
// TODO: Add tests for propertyH after confirming the `final` modifier case., check the property for details.

jsTypeWithSettersAndGetters.isHasFlag;
// @ts-expect-error
jsTypeWithSettersAndGetters.flag;
jsTypeWithSettersAndGetters.getFlag;
jsTypeWithSettersAndGetters.setFlag;

// ----------- Interfaces -------------------------------

class ImplementingJsTypeInterface implements JsTypeInterface {
    propertyA: string;

    methodFromJsTypeInterface(): string {
        return "";
    }

    methodOne(): string {
        return "";
    }

    methodTwo(): string {
        return "";
    }
}

const implementingJsTypeInterface = new ImplementingJsTypeInterface();
// @ts-expect-error
implementingJsTypeInterface.methodRenamed();
// @ts-expect-error
implementingJsTypeInterface.getPropertyA();
// @ts-expect-error
implementingJsTypeInterface.setPropertyA("text");
// TODO: What should we do with methods in interfaces and the method define a different namespace
implementingJsTypeInterface.methodFromJsTypeInterface();

// ------------------ Inheritance ------------------------

const jsTypeChild = new JsTypeChild();
// @ts-expect-error
jsTypeChild.childPropertyA;
// @ts-expect-error
jsTypeChild.childPropertyB;
jsTypeChild.childPropertyC;
// @ts-expect-error
jsTypeChild.childPropertyD;
jsTypeChild.childPropertyDRenamed;
jsTypeChild.parentPropertyE;
jsTypeChild.parentPropertyF = "text";

const parentPropertyFValue = jsTypeChild.parentPropertyF;
// @ts-expect-error
jsTypeChild.getParentPropertyF();
// @ts-expect-error
jsTypeChild.setParentPropertyF("text");
// @ts-expect-error
jsTypeChild.parentPropertyG;

jsTypeChild.childMethodOne();
// @ts-expect-error
jsTypeChild.childMethodTwo();
// @ts-expect-error
jsTypeChild.childMethodThree();
jsTypeChild.childMethodFourRenamed();
// @ts-expect-error
jsTypeChild.parentMethodFive();
// @ts-expect-error
jsTypeChild.parentIgnoredMethod();

jsTypeChild.interfaceOneMethodOne();
jsTypeChild.interfaceOneIgnoredMethod();
jsTypeChild.interfaceOneProperty = "text";
const interfaceOnePropertyValue = jsTypeChild.interfaceOneProperty;
// @ts-expect-error
jsTypeChild.getInterfaceOneProperty();
// @ts-expect-error
jsTypeChild.setInterfaceOneProperty("text");
// @ts-expect-error
jsTypeChild.getInterfaceOneGetterProperty();
const interfaceOneGetterPropertyValue =jsTypeChild.interfaceOneGetterProperty;
// @ts-expect-error
jsTypeChild.setInterfaceOneProperty("text");
jsTypeChild.interfaceOneSetterProperty ="text";
jsTypeChild.interfaceTwoMethodOne();
jsTypeChild.interfaceTwoIgnoredMethod();
jsTypeChild.interfaceTwoProperty = "text";
const interfaceTwoPropertyValue = jsTypeChild.interfaceTwoProperty;
// TODO: This generates a getter, the method itself should not be exported as is.
// @ts-expect-error
jsTypeChild.getInterfaceTwoProperty();
// TODO: This should be already exported as a property, setter method should not exist.
// @ts-expect-error
jsTypeChild.setInterfaceTwoProperty("text");

const jsTypeExtendsTsIgnoredSuperType = new JsTypeExtendsTsIgnoredSuperType();

jsTypeExtendsTsIgnoredSuperType.childProperty;
jsTypeExtendsTsIgnoredSuperType.property;
jsTypeExtendsTsIgnoredSuperType.doSomething();

// ------------------------ TsName ---------------------

class ImplementsJsInterfaceWithTsName implements JsInterfaceByTsName {
    methodOne(): string {
        return "";
    }
}

// --------------- Data types ----------------------

const jsTypeWithDataTypes = new JsTypeWithDataTypes();

// $ExpectType object
jsTypeWithDataTypes.objectProperty;
// $ExpectType Object
jsTypeWithDataTypes.jsObjectProperty;
// $ExpectType string[]
jsTypeWithDataTypes.stringArrayProperty;
// $ExpectType string[][]
jsTypeWithDataTypes.string2DArrayProperty;
// $ExpectType unknown
jsTypeWithDataTypes.stringListProperty;
// $ExpectType unknown
jsTypeWithDataTypes.mapProperty;
// $ExpectType Map<string, number>
jsTypeWithDataTypes.jsMapProperty;
// $ExpectType unknown
jsTypeWithDataTypes.setProperty;
// $ExpectType Set<string>
jsTypeWithDataTypes.jsSetProperty;
// $ExpectType string[]
jsTypeWithDataTypes.jsArrayProperty;
// $ExpectType { [key: string]: string; }
jsTypeWithDataTypes.jsPropertyMapProperty;
// $ExpectType any
jsTypeWithDataTypes.anyProperty;
// $ExpectType Number
jsTypeWithDataTypes.jsNumberProperty;
// $ExpectType { [key: string]: any; }[]
jsTypeWithDataTypes.propertyMapArray;
// $ExpectType { [key: string]: any; }[][]
jsTypeWithDataTypes.propertyMap2dArray;

// --------------- JsFunctions --------------------------

const jsFunctionsClient = new JsFunctionsClient();
jsFunctionsClient.useVoidFunction(() => {
    // To avoid empty function error since we are not interested in this error.
    const a="";
});
jsFunctionsClient.useFunctionWithArgs((id, name) => {
    // $ExpectType number
    const idType = id;
    // $ExpectType string
    const nameType = name;
});
jsFunctionsClient.useFunctionWithReturnType((id, name) => {
   return true;
});
// @ts-expect-error
jsFunctionsClient.useFunctionWithReturnType((id, name) => {
   return 1.0;
});
// $ExpectType (id: number, name: string) => boolean
const aFunction =jsFunctionsClient.useFunctionAndReturnFunction((id, name) => true);


// -------------- Generics -------------------

const genericJsTypeOfString = new GenericJsType<string>();
// $ExpectType string
genericJsTypeOfString.propertyOfT;
// $ExpectType string
genericJsTypeOfString.takesNothingReturnT();
// $ExpectType string
genericJsTypeOfString.takeTReturnT("text");
// @ts-expect-error
genericJsTypeOfString.takeTReturnT(1.0);

const genericJsTypeOfNumber = new GenericJsType<number>();
// $ExpectType number
genericJsTypeOfNumber.propertyOfT;
// $ExpectType number
genericJsTypeOfNumber.takesNothingReturnT();
// $ExpectType number
genericJsTypeOfNumber.takeTReturnT(1.0);
// @ts-expect-error
genericJsTypeOfNumber.takeTReturnT("text");

const genericJsTypeExtendsGenericJsType = new GenericJsTypeExtendsGenericJsTye<string>();
// $ExpectType string
genericJsTypeExtendsGenericJsType.childPropertyOfT;
// $ExpectType string
genericJsTypeExtendsGenericJsType.parentMethodOfT("string");
// @ts-expect-error
genericJsTypeExtendsGenericJsType.parentMethodOfT(1.0);
// $ExpectType string
genericJsTypeExtendsGenericJsType.propertyOfT;
// $ExpectType string
genericJsTypeExtendsGenericJsType.takesNothingReturnT();
// $ExpectType string
genericJsTypeExtendsGenericJsType.takeTReturnT("text");
// @ts-expect-error
genericJsTypeExtendsGenericJsType.takeTReturnT(1.0);

const jsTypeExtendsAbstractGenericJsType = new JsTypeExtendsAbstractGenericJsType();
// $ExpectType string
jsTypeExtendsAbstractGenericJsType.abstractTakesCReturnT(1.0);
// @ts-expect-error
jsTypeExtendsAbstractGenericJsType.abstractTakesCReturnT("text");

const jsTypeImplementingGenericInterfaces = new JsTypeImplementingGenericInterfaces<boolean>();
// $ExpectType string
jsTypeImplementingGenericInterfaces.genericInterfaceOneMethod();
// $ExpectType string
jsTypeImplementingGenericInterfaces.genericPropertyOne;
jsTypeImplementingGenericInterfaces.genericPropertyOne="text";
// @ts-expect-error
jsTypeImplementingGenericInterfaces.genericPropertyOne=1.0;

// $ExpectType number
jsTypeImplementingGenericInterfaces.genericInterfaceTwoMethod();
// $ExpectType number
jsTypeImplementingGenericInterfaces.genericPropertyTwo;
jsTypeImplementingGenericInterfaces.genericPropertyTwo=1.0;
// @ts-expect-error
jsTypeImplementingGenericInterfaces.genericPropertyTwo="text";

// $ExpectType boolean
jsTypeImplementingGenericInterfaces.genericInterfaceThreeMethod();
// $ExpectType boolean
jsTypeImplementingGenericInterfaces.genericPropertyThree;
jsTypeImplementingGenericInterfaces.genericPropertyThree = true;
// @ts-expect-error
jsTypeImplementingGenericInterfaces.genericPropertyThree = 1.0;

const nonGenericJsTypeExtendsGenericJsType = new NonGenericJsTypeExtendsGenericJsType();
// $ExpectType number
nonGenericJsTypeExtendsGenericJsType.childProperty;
// $ExpectType string
nonGenericJsTypeExtendsGenericJsType.takesNothingReturnT();
// $ExpectType string
nonGenericJsTypeExtendsGenericJsType.propertyOfT;
// $ExpectType string
nonGenericJsTypeExtendsGenericJsType.takesNothingReturnT();
// $ExpectType string
nonGenericJsTypeExtendsGenericJsType.takeTReturnT("text");
// @ts-expect-error
nonGenericJsTypeExtendsGenericJsType.takeTReturnT(1.0);

const dualGenericsJsType = new DualGenericsJsType<string, number>();
// $ExpectType string
dualGenericsJsType.propertyOfT;
// $ExpectType number
dualGenericsJsType.propertyOfC;
// $ExpectType string
dualGenericsJsType.takesNothingReturnT();
// $ExpectType string
dualGenericsJsType.takeTReturnT("text")
// $ExpectType number
dualGenericsJsType.takesNothingReturnC()
// $ExpectType number
dualGenericsJsType.takeCReturnC(1.0)
// $ExpectType string
dualGenericsJsType.takesCReturnT(1.0)
// $ExpectType number
dualGenericsJsType.takesTReturnC("text")

const usingInterfaceWithMethodTypeArgs = new UsingInterfaceWithMethodTypeArgs<number>();

usingInterfaceWithMethodTypeArgs.bar("string", "string");
usingInterfaceWithMethodTypeArgs.bar(1.0, "string");

usingInterfaceWithMethodTypeArgs.foo("string");
usingInterfaceWithMethodTypeArgs.foo(1.0);

usingInterfaceWithMethodTypeArgs.doSomething("string", 1.0, "string");
usingInterfaceWithMethodTypeArgs.doSomething(1.0, 1.0, 1.0);
// @ts-expect-error
usingInterfaceWithMethodTypeArgs.doSomething(1.0, "string", 1.0);

// ----------------- TsTypeDef -------------------
const enumSimulation = new EnumSimulation();

const enumClient = new EnumClient();

enumClient.useEnum(EnumSimulation.VALUE_1)
enumClient.useEnum(EnumSimulation.VALUE_2)
enumClient.useEnum(EnumSimulation.VALUE_3)
enumClient.useEnum("text")
// @ts-expect-error
enumClient.useEnum(1.0);
enumClient.useString("text");
enumClient.useString(EnumSimulation.VALUE_1);
enumClient.useJsTypeAsTypeReference(new UsedAsTypeRef());
// @ts-expect-error
enumClient.useJsTypeAsTypeReference({});

enumClient.values = {
    test: [EnumSimulation.VALUE_1, EnumSimulation.VALUE_2, EnumSimulation.VALUE_3],
    test2: ["string 1", "string 2", "string 3"],
    // @ts-expect-error
    test3: [1, 2, 3],
}

DayOfWeek.values();
class ItemDetailsChild extends ItemDetails {
    constructor() {
        super();
    }
}
const itemDetails = new ItemDetailsChild();
// TODO : The dtslint will always unwrap the type alias, will need to look for a different way to test this.
// We should expect the type to be ItemTypeType instead of string
// $ExpectType string
itemDetails.type;

// --------------------- JsNullable ---------------
const jsTypeWithJsNullableMembers = new JsTypeWithJsNullableMembers();

// @ts-expect-error
jsTypeWithJsNullableMembers.notNullable = null;
// @ts-expect-error
jsTypeWithJsNullableMembers.notNullable = undefined;
jsTypeWithJsNullableMembers.nullableProperty = null;
jsTypeWithJsNullableMembers.nullableProperty = undefined;

// @ts-expect-error
jsTypeWithJsNullableMembers.methodWithNullableParameter(null, null);
// @ts-expect-error
jsTypeWithJsNullableMembers.methodWithNullableParameter(null, undefined);
// @ts-expect-error
jsTypeWithJsNullableMembers.methodWithNullableParameter(undefined, undefined);
jsTypeWithJsNullableMembers.methodWithNullableParameter(null, "");
jsTypeWithJsNullableMembers.methodWithNullableParameter(undefined, "");

jsTypeWithJsNullableMembers.notNullable = jsTypeWithJsNullableMembers.notNullableMethod();
// @ts-expect-error
jsTypeWithJsNullableMembers.notNullable = jsTypeWithJsNullableMembers.nullableMethod();
jsTypeWithJsNullableMembers.nullableProperty = jsTypeWithJsNullableMembers.nullableMethod();

jsTypeWithJsNullableMembers.nullableStringJsArray = null;
jsTypeWithJsNullableMembers.nullableStringJsArray = undefined;
jsTypeWithJsNullableMembers.nullableStringJsArray = ["text1", "text2", "text3"];
// @ts-expect-error
jsTypeWithJsNullableMembers.nullableStringJsArray = [null, "text2", "text3"];
// @ts-expect-error
jsTypeWithJsNullableMembers.nullableStringJsArray = [undefined, "text2", "text3"];

// @ts-expect-error
jsTypeWithJsNullableMembers.jsArrayOfNullableStrings = null;
// @ts-expect-error
jsTypeWithJsNullableMembers.jsArrayOfNullableStrings = undefined;
jsTypeWithJsNullableMembers.jsArrayOfNullableStrings = ["text1", "text2", "text3"];
jsTypeWithJsNullableMembers.jsArrayOfNullableStrings = [null, "text2", "text3"];
jsTypeWithJsNullableMembers.jsArrayOfNullableStrings = [undefined, "text2", "text3"];


jsTypeWithJsNullableMembers.nullableArrayOfNullableStrings = null;
jsTypeWithJsNullableMembers.nullableArrayOfNullableStrings = undefined;
jsTypeWithJsNullableMembers.nullableArrayOfNullableStrings = ["text1", "text2", "text3"];
jsTypeWithJsNullableMembers.nullableArrayOfNullableStrings = [null, "text2", "text3"];
jsTypeWithJsNullableMembers.nullableArrayOfNullableStrings = [undefined, "text2", "text3"];

jsTypeWithJsNullableMembers.nullableMapOfNullableKeysAndValues = null;
jsTypeWithJsNullableMembers.nullableMapOfNullableKeysAndValues = undefined;
jsTypeWithJsNullableMembers.nullableMapOfNullableKeysAndValues = new Map([
    ['key',10],
    [null,20],
    [undefined,30],
    ['key2', null],
    ['key3', undefined]
]);

jsTypeWithJsNullableMembers.nullableMapOfNullableKeys = null;
jsTypeWithJsNullableMembers.nullableMapOfNullableKeys = undefined;
jsTypeWithJsNullableMembers.nullableMapOfNullableKeys = new Map([
    ['key',10],
    [null,20],
    [undefined,30]
]);
// @ts-expect-error
jsTypeWithJsNullableMembers.nullableMapOfNullableKeys = new Map([
    ['key',10],
    ['key2', null],
    ['key3', undefined]
]);


jsTypeWithJsNullableMembers.nullableMapOfNullableValues = null;
jsTypeWithJsNullableMembers.nullableMapOfNullableValues = undefined;
// @ts-expect-error
jsTypeWithJsNullableMembers.nullableMapOfNullableValues = new Map([
    ['key',10],
    [null,20],
    [undefined,30]
]);
jsTypeWithJsNullableMembers.nullableMapOfNullableValues = new Map([
    ['key',10],
    ['key2', null],
    ['key3', undefined]
]);

jsTypeWithJsNullableMembers.nullableStringArray=null;
// @ts-expect-error
jsTypeWithJsNullableMembers.nullableStringArray=undefined;
jsTypeWithJsNullableMembers.nullableStringArray=["text1", "text2"];
// @ts-expect-error
jsTypeWithJsNullableMembers.nullableStringArray=[null, "text2"];
// @ts-expect-error
jsTypeWithJsNullableMembers.nullableStringArray=[undefined, "text2"];

jsTypeWithJsNullableMembers.nullableString2dArray=null;
// @ts-expect-error
jsTypeWithJsNullableMembers.nullableString2dArray=undefined;
jsTypeWithJsNullableMembers.nullableString2dArray=[["text1", "text2"],["text1", "text2"]];
// @ts-expect-error
jsTypeWithJsNullableMembers.nullableString2dArray=[[null, "text2"],["text1", "text2"]];
// @ts-expect-error
jsTypeWithJsNullableMembers.nullableString2dArray=[[undefined, "text2"],["text1", "text2"]];

const jsInterfaceWithJsNullableSetGet:JsInterfaceWithJsNullableSetGet = {
    propertyThree:"string"
}

const extendsInterfaceWithJsNullableProperty = new ExtendsInterfaceWithJsNullableProperty();
extendsInterfaceWithJsNullableProperty.propertyOne;
extendsInterfaceWithJsNullableProperty.propertyTow;
extendsInterfaceWithJsNullableProperty.propertyThree;

// ----------------- TsInterface --------------------

const typeExtendingTsInterfaceType = new TypeExtendingTsInterfaceType();
// $ExpectType string
typeExtendingTsInterfaceType.property;

class JsTypeAsTsInterfaceChild implements JsTypeAsTsInterface {
    property: string;

    doSomething(): void {
        const x="";
    }
}

// ---------------------- Union Types --------------------------


class ImplementsUnionTypeApi implements UnionTypeApi {
    someFunction(param1: number | Array<number | null>, param2: number | Array<number | null> | null | undefined): number | Array<string> | null | undefined {
        return undefined;
    }

    arrays2dFunction(param1: Array<Array<number | Array<number | undefined | null>>>, param2: Array<Array<number | Array<number | undefined | null>>> | undefined | null): number | Array<Array<Array<number | Array<number | undefined | null>>>> | undefined | null {
        return undefined;
    }

    arraysFunction(param1: Array<number | Array<number | undefined | null>>, param2: Array<number | Array<number | undefined | null>> | undefined | null): number | Array<Array<number | Array<number | undefined | null>>> | undefined | null {
        return undefined;
    }

}

const implementsUnionTypeApi = new ImplementsUnionTypeApi();

implementsUnionTypeApi.someFunction(1.0, null);
implementsUnionTypeApi.someFunction(1.0, 1.0);
implementsUnionTypeApi.someFunction([1.0, 2.0], 1.0);
implementsUnionTypeApi.someFunction([1.0, null], 1.0);
implementsUnionTypeApi.someFunction(1.0, undefined);
implementsUnionTypeApi.someFunction(1.0, [1.0, 2.0]);
implementsUnionTypeApi.someFunction(1.0, [1.0, null]);
implementsUnionTypeApi.arraysFunction([1.0, [1.0, null, undefined]], [1.0, [1.0, null, undefined]]);
implementsUnionTypeApi.arraysFunction([1.0, [1.0, null, undefined]], null);
implementsUnionTypeApi.arraysFunction([1.0, [1.0, null, undefined]], undefined);
implementsUnionTypeApi.arrays2dFunction([[1.0, [1.0, null, undefined]]], [[1.0, [1.0, null, undefined]]]);
implementsUnionTypeApi.arrays2dFunction([[1.0, [1.0, null, undefined]]], null);
implementsUnionTypeApi.arrays2dFunction([[1.0, [1.0, null, undefined]]], undefined);

class ImplementsUnionTypeApiNumber implements UnionTypeApi {
    someFunction(param1: number | Array<number | null>, param2: number | Array<number | null> | null | undefined): number | Array<string> | null | undefined {
        return 1.0;
    }

    arrays2dFunction(param1: Array<Array<number | Array<number | undefined | null>>>, param2: Array<Array<number | Array<number | undefined | null>>> | undefined | null): number | Array<Array<Array<number | Array<number | undefined | null>>>> | undefined | null {
        return [[[1.0, [1.0, null, undefined]]]];
    }

    arraysFunction(param1: Array<number | Array<number | undefined | null>>, param2: Array<number | Array<number | undefined | null>> | undefined | null): number | Array<Array<number | Array<number | undefined | null>>> | undefined | null {
        return [[1.0, [1.0, null, undefined]]];
    }


}
class ImplementsUnionTypeApiArrayString implements UnionTypeApi {
    someFunction(param1: number | Array<number | null>, param2: number | Array<number | null> | null | undefined): number | Array<string> | null | undefined {
        return ["A", "B"];
    }

    arraysFunction(param1: Array<number | Array<number | undefined | null>>, param2: Array<number | Array<number | undefined | null>> | undefined | null): number | Array<Array<number | Array<number | undefined | null>>> | undefined | null {
        return 1.0;
    }

    arrays2dFunction(param1: Array<Array<number | Array<number | undefined | null>>>, param2: Array<Array<number | Array<number | undefined | null>>> | undefined | null): number | Array<Array<Array<number | Array<number | undefined | null>>>> | undefined | null {
        return 1.0;
    }

}
class ImplementsUnionTypeApiNull implements UnionTypeApi {
    someFunction(param1: number | Array<number | null>, param2: number | Array<number | null> | null | undefined): number | Array<string> | null | undefined {
        return null;
    }

    arrays2dFunction(param1: Array<Array<number | Array<number | undefined | null>>>, param2: Array<Array<number | Array<number | undefined | null>>>): number | Array<Array<Array<number | Array<number | undefined | null>>>> | undefined | null {
        return null;
    }

    arraysFunction(param1: Array<number | Array<number | undefined | null>>, param2: Array<number | Array<number | undefined | null>>): number | Array<Array<number | Array<number | undefined | null>>> | undefined | null {
        return null;
    }



}
class ImplementsUnionTypeApiUndefined implements UnionTypeApi {
    someFunction(param1: number | Array<number | null>, param2: number | Array<number | null> | null | undefined): number | Array<string> | null | undefined {
        return undefined;
    }

    arrays2dFunction(param1: Array<Array<number | Array<number | undefined | null>>>, param2: Array<Array<number | Array<number | undefined | null>>>): number | Array<Array<Array<number | Array<number | undefined | null>>>> | undefined | null {
        return undefined;
    }

    arraysFunction(param1: Array<number | Array<number | undefined | null>>, param2: Array<number | Array<number | undefined | null>>): number | Array<Array<number | Array<number | undefined | null>>> | undefined | null {
        return undefined;
    }
}