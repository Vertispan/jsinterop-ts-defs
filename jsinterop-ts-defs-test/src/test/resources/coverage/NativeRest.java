package coverage;

import jsinterop.annotations.JsType;

/** TypeScript sees it through its own declaration, lib.NativeRest. */
@JsType(isNative = true, namespace = "lib")
public interface NativeRest {
  void add(String... names);
}
