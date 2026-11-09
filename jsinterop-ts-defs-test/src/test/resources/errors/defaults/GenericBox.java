package errors.defaults;

import jsinterop.annotations.JsType;

@JsType
public interface GenericBox<T> {
  T get();
}
