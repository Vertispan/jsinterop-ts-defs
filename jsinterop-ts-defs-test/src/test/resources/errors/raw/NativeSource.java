package errors.raw;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface NativeSource<T> {
  T get();
}
