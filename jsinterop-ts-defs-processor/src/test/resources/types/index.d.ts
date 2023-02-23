// Minimum TypeScript Version: 4.3

export interface TsInterfaceExtendingTsInterface extends TypeAsTsInterface {
	firstName:string;
	fooMember:string;
}
export interface TypeAsTsInterface extends com.vertispan.tsdefs.types.FooInterface {
	firstName:string;
	fooMember:string;

	get(index:unknown):string;
}

export class JsTypeExtendingTsInterface implements TypeAsTsInterface {
	firstName:string;
	fooMember:string;

	constructor();

	shouldBeExportedInInheritors():string;
	get(index:unknown):string;
}


export namespace com.vertispan.tsdefs.types.generics {

	export interface GenericBarInterface<T, C> extends GenericBooInterface<T> {	}
	export interface GenericBooInterface<T> {	}
}

export namespace com.vertispan.tsdefs.properties {

	export class OtherJsType {
		stringProperty:string;

		constructor();
	}

	export class NonJsTypeWithExportableProperties {
		shouldBeExported:string;
		privateButExported:string;

		constructor();
	}


	export class JsTypeWithProperties {
		static GLOBAL_EDITABLE:string;
		static readonly GLOBAL_READONLY:string;
		intProperty:number;
		integerProperty:unknown;
		doubleProperty:number;
		doubleWrapperProperty:number;
		booleanProperty:boolean;
		booleanWrapperProperty:boolean;
		floatProperty:unknown;
		floatWrapperProperty:unknown;
		voidType:void;
		stringProperty:string;
		otherJsTypeProperty:OtherJsType;
		differentName:string;
		privateButExported:string;

		constructor();
	}

}

export namespace com {

	export class OtherNameSpace {
		propInOtherNamespace:string;
	}

}

export namespace com.vertispan.tsdefs.types.defaultmethod {

	export interface InterfaceWithDefaultMethods {
		doSomeDefaultStuff():string;
		doSomeOtherDefaultStuff():string;
	}

	export class TypeImplementsInterfaceWithDefaultMethods implements InterfaceWithDefaultMethods {
		constructor();

		doSomeOtherDefaultStuff():string;
		doSomeDefaultStuff():string;
	}

}

export namespace com.vertispan.tsdefs.types.inline {

	export class TypeWithInlineFunctions {
		constructor();

		doSomething(sampleFunc:(arg0:string)=>string):string;
		doSomethingElse(func:(arg0:number)=>string):void;
	}

}

export namespace com.vertispan.tsdefs.jsfunctions {

	export class JsFunctionsClient {
		constructor();

		useVoidFunction(takesVoidReturnsVoidJsFunction:()=>void):void;
		useFunctionWithArgs(takesArgsReturnsVoidJsFunction:(id:number,name:string)=>void):void;
		useFunctionWithReturnType(takesArgsReturnsTypeJsFunction:(id:number,name:string)=>boolean):void;
		useFunctionAndReturnFunction(takesArgsReturnsTypeJsFunction:(id:number,name:string)=>boolean):(id:number,name:string)=>boolean;
	}

}

export namespace dh {

	export interface ParentB {	}
	export interface TreeRow {	}
	export interface WidgetExportedObject {
		fetch():Promise<string[][]>;
		fetchWildCard():Promise<any>;
	}

	export class DiffType {
		static otherMethod():void;
	}

	export class ParentA {
		protected constructor();

		doSomething():string;
	}

}

export namespace com.vertispan.tsdefs.generics {

	export interface GenericInterfaceThree<T> {
		genericPropertyThree:T;

		genericInterfaceThreeMethod():T;
	}
	export interface GenericInterfaceOne<T> {
		genericPropertyOne:T;

		genericInterfaceOneMethod():T;
	}
	export interface GenericInterfaceTwo<T> {
		genericPropertyTwo:T;

		genericInterfaceTwoMethod():T;
	}

	export class JsTypeImplementingGenericInterfaces<T> implements GenericInterfaceOne<string>, GenericInterfaceTwo<number>, GenericInterfaceThree<T> {
		genericPropertyOne:string;
		genericPropertyThree:T;
		genericPropertyTwo:number;

		constructor();

		genericInterfaceOneMethod():string;
		genericInterfaceTwoMethod():number;
		genericInterfaceThreeMethod():T;
	}

	export class GenericJsType<T> {
		propertyOfT:T;

		constructor();

		takesNothingReturnT():T;
		takeTReturnT(parameterOfT:T):T;
	}

	export class DualGenericsJsType<T, C> {
		propertyOfT:T;
		propertyOfC:C;

		constructor();

		takesNothingReturnT():T;
		takeTReturnT(parameterOfT:T):T;
		takesNothingReturnC():C;
		takeCReturnC(parameterOfC:C):C;
		takesCReturnT(propertyOfC:C):T;
		takesTReturnC(propertyOfT:T):C;
	}

	export class JsTypeExtendsAbstractGenericJsType extends AbstractGenericJsType<string, number> {
		constructor();

		abstractTakesCReturnT(parameterOfC:number):string;
	}

	export abstract class AbstractGenericJsType<T, C> {
		constructor();

		abstract abstractTakesCReturnT(parameterOfC:C):T;
	}

	export class GenericJsTypeExtendsGenericJsTye<T> extends GenericJsType<T> {
		childPropertyOfT:T;

		constructor();

		parentMethodOfT(parameterOfT:T):T;
	}

	export class NonGenericJsTypeExtendsGenericJsType extends GenericJsType<string> {
		childProperty:number;

		constructor();

		takesNothingReturnT():string;
	}

}

export namespace com.vertispan.tsdefs.interfaces {

	export interface JsTypeInterface {
		propertyA:string;

		methodOne():string;
		methodTwo():string;
		methodFromJsTypeInterface():string;
	}
}

export namespace dh.diffns {

	export class OtherClient {
		/**
		* This is a sample class to test all kind of cases for exporting JsInterop to TS Type definition
		* This is another line to see how is this being processed by the annotation processor
		*
		* @param arg just testing params
		* @return String testing return tag
		* @see {@link FooInterface}
		*/
		static differentNs(arg:number):string;
	}

}

export namespace com.vertispan.tsdefs.jsnullable {

	export class JsTypeWithJsNullableMembers {
		notNullable:string;
		nullableProperty:string|null|undefined;
		nullableStringArray:string[]|null|undefined;
		nullableString2dArray:string[][]|null|undefined;
		nullableStringJsArray:Array<string>|null|undefined;
		jsArrayOfNullableStrings:Array<string|null|undefined>;
		nullableArrayOfNullableStrings:Array<string|null|undefined>|null|undefined;
		nullableMapOfNullableKeysAndValues:Map<string|null|undefined, number|null|undefined>|null|undefined;
		nullableMapOfNullableKeys:Map<string|null|undefined, number>|null|undefined;
		nullableMapOfNullableValues:Map<string, number|null|undefined>|null|undefined;

		constructor();

		notNullableMethod():string;
		nullableMethod():string|null|undefined;
		methodWithNullableParameter(nullable:string|null|undefined, notNullable:string):string;
	}

}

export namespace com.vertispan.tsdefs.types.self {

	export interface QueryConnectable<Self extends QueryConnectable<Self>> {
		prop():string;
	}

	export class CoreClient implements QueryConnectable<CoreClient> {
		propx:string;

		constructor();

		prop():string;
	}

}

export namespace com.vertispan.tsdefs.types.tsinterface {

	export interface TsInterfaceWithFields {
		name:string;
		otherName:string;

		get prop():string;
	}
}


export namespace com.vertispan.tsdefs.constructors {

	export class NonJsTypeWithJsConstructor {
		property:string;

		constructor(property:string);
	}

	export class JsTypeWithJsIgnoredAndNonIgnoredConstructor {
		property:string;

		constructor(property:string, otherProperty:string);
	}

	export class JsTypeWithJsIgnoredConstructor {
		property:string;

		protected constructor();
	}

	export class NonJsTypeWithJsIgnoredConstructor {
		property:string;

		protected constructor();
	}

	export class JsTypeWithNoConstructors {
		property:string;

		constructor();
	}

	export class NonJsTypeWithJsIgnoredAndNonIgnoredConstructor {
		property:string;

		constructor(property:string, otherProperty:string);
	}

	export class JsTypeWithJsIgnoredConstructorAndJsIgnoredDefaultConstructor {
		property:string;

		protected constructor();
	}

	export class JsTypeWithJsIgnoredConstructorAndDefaultConstructor {
		property:string;

		constructor();
	}

	export class JsTypeWithConstructor {
		property:string;

		constructor(property:string);
	}

}

export namespace com.vertispan.tsdefs.types.temp {

	export interface NonTsIgnoredInterfaceInheritTsIgnoredInterface {
		fromNonTsIgnoredInterface():string;
		fromTsIgnoredInterface():string;
	}
}

export namespace com.vertispan.tsdefs.methods {

	export class JsTypeWithMethods {
		constructor();

		takesNothingReturnVoid():void;
		takesNothingReturnVoidRenamed():void;
		takesDoubleReturnString(value:number):string;
		takesMultipleParamsReturnString(stringParam:string, doubleParam:number, booleanParam:boolean):string;
		protected protectedMethod():string;
		privateMethodButExported(value:number):string;
	}

	export class NonJsTypeWithMethods {
		constructor();

		takesNothingReturnVoidExported():void;
		takesNothingReturnVoidRenamed():void;
		protected protectedMethod():string;
		privateMethodButExported(value:number):string;
	}

	export class JsTypeWithStaticMethods {
		constructor();

		static doSomethingStatic():void;
		doSomething():void;
	}

}

export namespace com.vertispan.differentNameSpace {

	export class OtherType {
		theOtherProperty:string;

		doSomething():string;
		doSomethingElse():string;
	}

	export class DifferentJsTypeWithPropertiesAndNameAndNameSpace {
		property:string;
		fourthProperty:string;
		theThirdProperty:string;

		constructor();
	}

}

export namespace com.vertispan.tsdefs.inheritance {

	export interface JsInterfaceOne {
		interfaceOneProperty:string;

		interfaceOneMethodOne():string;
		get interfaceOneGetterProperty():string;
		set interfaceOneSetterProperty(interfaceOneSetterProperty:string);
	}
	export interface JsInterfaceTwo {	}

	export class JsTypeChild extends JsTypeParent implements JsInterfaceOne, JsInterfaceTwo {
		childPropertyC:string;
		childPropertyDRenamed:string;
		parentPropertyE:string;
		parentPropertyF:string;
		interfaceTwoProperty:string;
		interfaceOneProperty:string;

		constructor();

		childMethodOne():string;
		childMethodFourRenamed():void;
		interfaceOneMethodOne():string;
		interfaceOneIgnoredMethod():string;
		interfaceTwoMethodOne():string;
		interfaceTwoIgnoredMethod():string;
		get interfaceOneGetterProperty():string;
		set interfaceOneSetterProperty(interfaceOneSetterProperty:string);
	}

	export class JsTypeExtendsTsIgnoredSuperType {
		childProperty:string;
		property:string;

		constructor();

		doSomething():void;
	}

	export class JsTypeParent {
		parentPropertyC:string;
		parentPropertyDRenamed:string;
		parentPropertyF:string;

		constructor();

		parentMethodOne():string;
		parentMethodFourRenamed():void;
	}

}

export namespace com.vertispan.tsdefs.types {

	export interface InterfaceWithFunctions {
		returnNothing():void;
		returnNothingAndTakeArgs(firstName:string, id:unknown):void;
		returnBooleanAndTakeArgs(firstName:string, id:unknown):boolean;
	}
	export interface GenericFooInterface<T, C> {	}
	export interface GenericBooInterface<T> {	}
	export interface FooInterface {
		fooMember:string;

		shouldBeExportedInInheritors():string;
	}
	export interface GenericBarInterface<T, C> extends GenericBooInterface<T> {	}

	export class NotJsType implements FooInterface {
		firstName:string;
		fooMember:string;

		protected constructor();

		get(index:unknown):string;
		shouldBeExportedInInheritors():string;
	}

	export class ChildMultiTypeArgs<T, C, V, A> {
		prop:string;

		constructor();
	}

	export class NoJsTypeDuplicateMethod {
		protected constructor();

		doSomething(id:number):string;
	}


	export class ChildSingleTypeArg<T> extends OtherNs.NoArgsType {
		prop:string;

		constructor();
	}

	/**
	* This is a sample class to test all kind of cases for exporting JsInterop to TS Type definition
	* This is another line to see how is this being processed by the annotation processor
	*
	* @return String testing return tag
	* @see {@link FooInterface}
	*/
	export class Client implements FooInterface {
		static readonly ACONSTANT:string;
		initialName:string;
		readonly lastName:string;
		pFloatF:unknown;
		intField:number;
		floatF:unknown;
		doubleF:number;
		doublePf:number;
		booleanF:boolean;
		booleanPf:boolean;
		voidF:void;
		privateButExported:string;
		childNoTypeArgs:OtherNs.NoArgsType;
		childSingleTypeArg:ChildSingleTypeArg<OtherNs.NoArgsType>;
		childMultiTypeArgs:ChildMultiTypeArgs<OtherNs.NoArgsType, ChildSingleTypeArg<string>, unknown, boolean>;
		addresses:string[];
		array2d:string[][];
		childNoTypeArgsList:unknown;
		childSingleTypeArgList:unknown;
		setField:unknown;
		mapField:unknown;
		fooMember:string;
		alterGetterName:string;
		someValue:string;
		shouldExportSetterAndGetter:string;

		constructor(initialName:string, lastName:string);

		/**
		* @deprecated
		*/
		doSomething():void;
		/**
		* This is a sample class to test all kind of cases for exporting JsInterop to TS Type definition
		* This is another line to see how is this being processed by the annotation processor
		*
		* @param name just testing params
		* @return String testing return tag
		* @see {@link FooInterface}
		* @deprecated
		*/
		doSomethingWithArgs(name:string, id:number):void;
		doSomethingWithArgsAndReturnType(name:string, index:number):string;
		getShouldExportGetter():unknown;
		privateMethodBurExported():string;
		takesFunction(func:(arg0:number)=>string):void;
		shouldBeExportedInInheritors():string;
		set shouldExportSetter(shouldExportSetter:unknown);
	}

	export class UsesEnum {
		constructor();

		doSomething(x:ChartTypeType):void;
	}

	export class ExtendsFromAnother extends Client {
		alterGetterName:string;
		someValue:string;
		shouldExportSetterAndGetter:string;

		constructor(lastName:string);

		set shouldExportSetter(shouldExportSetter:unknown);
	}

	export class ExtendsGeneric<T, C> extends ChildSingleTypeArg<T> implements GenericFooInterface<T, C>, GenericBooInterface<C> {
		constructor();
	}


	type ChartTypeType = number;
	export class ChartType {
		static readonly XY:ChartTypeType;
		static readonly PIE:ChartTypeType;
		static readonly BARS:ChartTypeType;
	}

}

export namespace com.vertispan.tsdefs.types.tsignore {

	export interface InterfaceA extends InterfaceB {
		methodA():string;
		methodB():string;
	}
	export interface NonTsIgnoredInterfaceInheritTsIgnoredInterface {
		fromNonTsIgnoredInterface():string;
		fromTsIgnoredInterface():string;
	}
	export interface InterfaceB {
		methodB():string;
	}

	export class TypeInheritsNonTsIgnoredInterface implements NonTsIgnoredInterfaceInheritTsIgnoredInterface {
		constructor();

		fromTsIgnoredInterface():string;
		fromNonTsIgnoredInterface():string;
	}

	export class TypeInheritsTsIgnoredInterface {
		constructor();

		fromTsIgnoredInterface():string;
	}

}

export namespace com.vertispan.tsdefs.jsclasses {

	export class FinalJsType {
		property:string;

		constructor();

		doSomething(name:string):void;
	}

}

export namespace com.vertispan.tsnamesspace {

	export class JsTypeByTsNameAndNamespace {
		propertyA:string;

		constructor();
	}

	export class JsTypeByTsNameAndNamespaceInsteadOfJsName {
		propertyA:string;

		constructor();
	}

}

export namespace com.vertispan.tsdefs.tstypedef {

	export class EnumClient {
		testMap:Map<string, number>;
		values:{ [key: string]: Array<EnumSimulationType>; };

		constructor();

		useEnum(param:EnumSimulationType):void;
		useString(param:string):void;
		useJsTypeAsTypeReference(param:UsedAsTypeRef):void;
	}

	export class UsedAsTypeRef {
		property:string;

		constructor();
	}


	type EnumSimulationType = string;
	export class EnumSimulation {
		static readonly VALUE_1:EnumSimulationType;
		static readonly VALUE_2:EnumSimulationType;
		static readonly VALUE_3:EnumSimulationType;
	}

}

export namespace com.vertispan.tsdefs.settergetter {

	export class JsTypeWithSettersAndGetters {
		propertyD:string;
		propertyC:string;

		constructor(propertyH:string);

		getPropertyA():string;
		setPropertyA(propertyA:string):void;
		getPropertyB():string;
		setPropertyB(propertyB:string):void;
		getPropertyE():string;
		setPropertyF(propertyF:string):void;
		getPropertyH():string;
		get renamedPropertyG():string;
		get isHasFlag():boolean;
		get getFlag():string;
		set setFlag(setFlag:string);
		set propertyH(propertyH:string);
	}

}

export namespace com.vertispan.tsdefs.types.varargs {

	export class JsTypeWithVarargsMethod {
		constructor();

		doSomething(x:number[], ...args:string[]):string;
		doSomething2(...args:string[]):string;
		doSomething3(args:string[]):string;
	}

}

export namespace OtherNs {

	export class NoArgsType {
		constructor();

		static someMethod():void;
	}

}

export namespace com.vertispan.tsdefs.tsname {

	export interface JsInterfaceByTsName {
		methodOne():string;
	}

	export class JsTypeByTsNameInsteadOfJsName {
		propertyA:string;

		constructor();
	}

	export class JsTypeByTsName {
		propertyA:string;

		constructor();
	}

}

export namespace com.vertispan.tsdefs.types.constructors {

	export class NoJsTypeD {
		propC:string;

		protected constructor();
	}

	export class NoJsTypeC {
		propC:string;

		protected constructor();
	}

	export class TypeA {
		propA:string;

		protected constructor();
	}

	export class TypeB {
		probB:string;

		constructor();
	}

	export class NoJsTypeE {
		propC:string;

		constructor();
	}

}

export namespace com.vertispan.tsdefs.tsinterface {

	export interface TsInterfaceType {
		property:string;

		doSomething():void;
	}
	export interface JsTypeAsTsInterface {
		property:string;

		doSomething():void;
	}

	export class TypeExtendingTsInterfaceType implements TsInterfaceType {
		property:string;

		constructor();

		doSomething():void;
	}

}

export namespace com.vertispan.tsdefs.types.returntype {

	export class MethodReturnType {
		constructor();

		doSomething():string|null|undefined;
	}

}

export namespace com.vertispan.tsdefs.datatypes {

	export class JsTypeWithDataTypes {
		objectProperty:object;
		jsObjectProperty:Object;
		stringArrayProperty:string[];
		string2DArrayProperty:string[][];
		stringListProperty:unknown;
		mapProperty:unknown;
		jsMapProperty:Map<string, number>;
		setProperty:unknown;
		jsSetProperty:Set<string>;
		jsArrayProperty:Array<string>;
		jsPropertyMapProperty:{ [key: string]: string; };
		anyProperty:any;
		jsNumberProperty:Number;

		constructor();
	}

}

