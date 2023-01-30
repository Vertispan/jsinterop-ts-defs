package com.vertispan.tsdefs.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithSingleNotIgnoredConstructors {

    public JsTypeWithSingleNotIgnoredConstructors(){
    }

    @JsIgnore
    public JsTypeWithSingleNotIgnoredConstructors(String arg0){
    }

}