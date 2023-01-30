package com.vertispan.tsdefs.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithMultipleIgnoredConstructors {

    @JsIgnore
    public JsTypeWithMultipleIgnoredConstructors(){
    }

    @JsIgnore
    public JsTypeWithMultipleIgnoredConstructors(String lastName) {
    }
}