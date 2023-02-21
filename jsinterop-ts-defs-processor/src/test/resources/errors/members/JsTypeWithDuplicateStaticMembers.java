package com.vertispan.tsdefs.types;

import jsinterop.annotations.*;

@JsType
public class JsTypeWithDuplicateStaticMembers {
    public static String member0;
    public static String member0(int x){
     return "";
    }
}