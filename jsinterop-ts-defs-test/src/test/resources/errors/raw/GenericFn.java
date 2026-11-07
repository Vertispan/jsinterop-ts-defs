package errors.raw;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface GenericFn<T> {
  void call(T t);
}
