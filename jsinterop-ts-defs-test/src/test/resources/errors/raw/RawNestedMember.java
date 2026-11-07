package errors.raw;

import jsinterop.annotations.JsType;

@JsType
@SuppressWarnings("rawtypes")
public abstract class RawNestedMember {
  public abstract Holder.Inner raw();
}
