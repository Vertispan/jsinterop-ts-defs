package errors.raw;

import jsinterop.annotations.JsType;

@JsType
@SuppressWarnings("rawtypes")
public abstract class TwoRawTypesMember {
  public abstract Pair<Container, GenericBase> both();
}
