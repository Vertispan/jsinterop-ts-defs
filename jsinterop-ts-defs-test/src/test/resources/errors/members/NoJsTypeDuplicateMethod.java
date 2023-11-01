package com.vertispan.tsdefs.tests.types;

import jsinterop.annotations.JsMethod;

public class NoJsTypeDuplicateMethod {

    public String doSomething(){
        return null;
    }

    @JsMethod
    public String doSomething(int id){
        return null;
    }
}
