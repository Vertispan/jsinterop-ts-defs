package com.vertispan.tsdefs.tests.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithDuplicateFieldAndJsPropertyName {
    public String member0;
    @JsProperty(name="member0")
    public String member1;
}