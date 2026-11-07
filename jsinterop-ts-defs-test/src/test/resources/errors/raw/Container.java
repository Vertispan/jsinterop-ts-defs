package errors.raw;

import jsinterop.annotations.JsType;

@JsType
public interface Container<T> {
  T get();
}
