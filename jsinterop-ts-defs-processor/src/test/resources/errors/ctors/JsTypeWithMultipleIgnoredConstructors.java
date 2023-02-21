package com.vertispan.tsdefs.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithMultipleIgnoredConstructors {
    public void go() {}

    @JsIgnore
    public JsTypeWithMultipleIgnoredConstructors(){
    }

    @JsIgnore
    public JsTypeWithMultipleIgnoredConstructors(String lastName) {
    }

    public static class SubclassWithParentIgnoredConstructor extends JsTypeWithMultipleIgnoredConstructors {
        @JsMethod
        public void stop() {}

    }
}