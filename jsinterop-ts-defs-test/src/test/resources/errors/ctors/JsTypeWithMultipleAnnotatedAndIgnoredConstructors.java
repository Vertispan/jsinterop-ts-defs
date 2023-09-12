package com.vertispan.tsdefs.tests.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithMultipleAnnotatedAndIgnoredConstructors {

    @JsConstructor
    public JsTypeWithMultipleAnnotatedAndIgnoredConstructors(){
    }

    @JsConstructor
    public JsTypeWithMultipleAnnotatedAndIgnoredConstructors(String lastName) {
    }

    @JsIgnore
    public JsTypeWithMultipleAnnotatedAndIgnoredConstructors(String arg0, int arg1){
    }

    @JsIgnore
    public JsTypeWithMultipleAnnotatedAndIgnoredConstructors(String arg0, int arg1, boolean arg2) {
    }
}