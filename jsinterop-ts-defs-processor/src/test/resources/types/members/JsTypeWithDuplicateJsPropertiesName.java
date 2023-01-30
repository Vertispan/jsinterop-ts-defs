package com.vertispan.tsdefs.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithDuplicateJsPropertiesName {

    @JsProperty(name="member02")
    public String member0;
    @JsProperty(name="member02")
    public String member1(){

    }
}