package com.vertispan.tsdefs.tests.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithPublicFieldAndPublicAccessor {
    public String member0;

    public String getMember0() {
        return member0;
    }
}