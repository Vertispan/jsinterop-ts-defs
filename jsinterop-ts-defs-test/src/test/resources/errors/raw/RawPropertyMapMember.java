package errors.raw;

import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType
@SuppressWarnings("rawtypes")
public abstract class RawPropertyMapMember {
  public abstract JsPropertyMap raw();
}
