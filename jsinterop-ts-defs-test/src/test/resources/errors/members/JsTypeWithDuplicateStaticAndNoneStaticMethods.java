package com.vertispan.tsdefs.tests.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithDuplicateStaticAndNoneStaticMethods {
    public static String doSomething(int x){
     return "";
    }
    public String doSomething(){
        return "";
    }
}