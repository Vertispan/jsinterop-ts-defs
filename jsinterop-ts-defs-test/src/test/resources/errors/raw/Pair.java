package errors.raw;

import jsinterop.annotations.JsType;

@JsType
public interface Pair<A, B> {
  A first();

  B second();
}
