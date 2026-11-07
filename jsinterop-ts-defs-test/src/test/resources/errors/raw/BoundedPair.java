package errors.raw;

import jsinterop.annotations.JsType;

/** Only the second parameter is bounded. */
@JsType
public interface BoundedPair<A, B extends Number> {
  A first();

  B second();
}
