package errors.raw;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface NativeThen<T> {
  void then(T value);
}
