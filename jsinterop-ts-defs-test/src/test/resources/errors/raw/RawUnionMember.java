package errors.raw;

import jsinterop.annotations.JsType;

@JsType
@SuppressWarnings("rawtypes")
public abstract class RawUnionMember {
  public abstract GenericUnion raw();
}
