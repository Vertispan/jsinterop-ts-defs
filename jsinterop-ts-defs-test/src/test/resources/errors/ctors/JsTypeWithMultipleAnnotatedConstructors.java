package com.vertispan.tsdefs.tests.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithMultipleAnnotatedConstructors {

    @JsConstructor
    public JsTypeWithMultipleAnnotatedConstructors(){
    }

    @JsConstructor
    public JsTypeWithMultipleAnnotatedConstructors(String lastName) {
    }
}