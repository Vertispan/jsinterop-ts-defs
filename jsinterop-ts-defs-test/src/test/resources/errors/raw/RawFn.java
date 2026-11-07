package errors.raw;

import jsinterop.annotations.JsFunction;

@JsFunction
@SuppressWarnings("rawtypes")
public interface RawFn {
  Container apply(
      Container c);
}
