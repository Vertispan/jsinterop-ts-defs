package warnings.overrides;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public interface Shape {
  @JsProperty(name = "size")
  Object getSize();

  @JsMethod(name = "describe")
  Object text();
}
