package errors.unions;

import com.vertispan.tsdefs.annotations.TsUnion;
import com.vertispan.tsdefs.annotations.TsUnionMember;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
@TsUnion(anonymous = false, name = "?")
public interface SuppressedExplicitUnion {
  @JsOverlay
  @TsUnionMember
  default String asString() {
    return null;
  }
}
