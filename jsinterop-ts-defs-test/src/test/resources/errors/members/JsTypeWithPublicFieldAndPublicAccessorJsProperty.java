package com.vertispan.tsdefs.tests.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithPublicFieldAndPublicAccessorJsProperty {
    public String member0;

    @JsProperty(name="member0")
    public String getSomething() {
        return member0;
    }
}