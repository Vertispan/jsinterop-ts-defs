package names;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType
public interface RenamedApi {
  @JsMethod(name = "bar")
  void foo();
}
