package warnings.overrides;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType
public interface RenamedText {
  @JsMethod(name = "d")
  Object text();
}
