package types.functions;

import jsinterop.annotations.JsFunction;

/** Not used by any JsType. */
@JsFunction
public interface UnusedGenericMethodFn {
  <U> U apply(U u);
}
