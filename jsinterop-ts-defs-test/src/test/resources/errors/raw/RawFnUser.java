package errors.raw;

import jsinterop.annotations.JsType;

@JsType
public abstract class RawFnUser {
  public RawFn field;

  public abstract void take(RawFn fn);
}
