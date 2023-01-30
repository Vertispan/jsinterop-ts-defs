package com.vertispan.tsdefs.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithIgnoredConstructorsExceptOneAnnotated {

    @JsConstructor
    public JsTypeWithIgnoredConstructorsExceptOneAnnotated(){
    }

    @JsIgnore
    public JsTypeWithIgnoredConstructorsExceptOneAnnotated(String arg0){
    }

    public JsTypeWithIgnoredConstructorsExceptOneAnnotated(String arg0, String arg1){
    }

}