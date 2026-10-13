package errors.unions;

import com.vertispan.tsdefs.annotations.TsUnion;
import com.vertispan.tsdefs.annotations.TsUnionMember;
import jsinterop.annotations.JsOverlay;

@TsUnion(anonymous = false, name = "?")
public interface SuppressedExplicitUnion {
  @JsOverlay
  @TsUnionMember
  default String asString() {
    return null;
  }
}
