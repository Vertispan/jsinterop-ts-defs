package coverage;

import jsinterop.annotations.JsType;

@JsType
public interface Converter {
  <X> X conv(X x);
}
