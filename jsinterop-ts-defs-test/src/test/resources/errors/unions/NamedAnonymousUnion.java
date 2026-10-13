package errors.unions;

import com.vertispan.tsdefs.annotations.TsUnion;
import com.vertispan.tsdefs.annotations.TsUnionMember;
import jsinterop.annotations.JsOverlay;

@TsUnion(name = "InvalidAnonymousUnion")
public interface NamedAnonymousUnion {
  @JsOverlay
  @TsUnionMember
  default String asString() {
    return null;
  }
}
