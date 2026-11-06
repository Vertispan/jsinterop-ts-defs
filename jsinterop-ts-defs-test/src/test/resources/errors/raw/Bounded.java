package errors.raw;

import jsinterop.annotations.JsType;

@JsType
public interface Bounded<T extends Number> {
  T get();
}
