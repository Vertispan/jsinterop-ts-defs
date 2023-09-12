/** Copyright (c) 2016-2022 Deephaven Data Labs and Patent Pending */
package com.vertispan.tsdefs.tests.types.dh;

import com.vertispan.tsdefs.annotations.TsInterface;
import com.vertispan.tsdefs.annotations.TsName;
import elemental2.promise.Promise;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;

@TsInterface
@TsName(namespace = "com.vertispan", name = "WidgetExportedObject")
public class JsWidgetExportedObject {

  @JsProperty
  public String getType() {
    return "";
  }

  @JsMethod
  public Promise<String[][]> fetch() {
    return null;
  }

  @JsMethod
  public Promise<?> fetchWildCard() {
    return null;
  }
}
