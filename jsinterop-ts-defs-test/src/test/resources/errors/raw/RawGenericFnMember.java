package errors.raw;

import jsinterop.annotations.JsType;

@JsType
@SuppressWarnings("rawtypes")
public abstract class RawGenericFnMember {
  public abstract void take(GenericFn fn);
}
