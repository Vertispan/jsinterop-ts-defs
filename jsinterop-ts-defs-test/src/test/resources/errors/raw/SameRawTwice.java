package errors.raw;

import jsinterop.annotations.JsType;

@JsType
@SuppressWarnings("rawtypes")
public abstract class SameRawTwice {
  public abstract Container first();

  public abstract Container second();
}
