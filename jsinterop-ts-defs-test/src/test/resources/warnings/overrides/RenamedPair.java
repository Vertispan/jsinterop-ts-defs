package warnings.overrides;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType
public interface RenamedPair {
  @JsMethod(name = "x")
  Object foo();

  @JsMethod(name = "y")
  Item bar();
}
