package errors.raw;

import com.vertispan.tsdefs.annotations.TsUnion;
import com.vertispan.tsdefs.annotations.TsUnionMember;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
@TsUnion
public interface GenericUnion<T> {
  @JsOverlay
  @TsUnionMember
  default Double asNumber() {
    return null;
  }

  @JsOverlay
  @TsUnionMember
  default Container<T> asContainer() {
    return null;
  }
}
