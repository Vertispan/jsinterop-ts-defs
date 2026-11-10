package coverage;

import com.vertispan.tsdefs.annotations.TsIgnore;
import jsinterop.annotations.JsType;

/** Its v() isn't emitted, so it can't stand for IgnoredValue.v(). */
@JsType
public interface IgnoredOverride extends IgnoredValue {
  @TsIgnore
  @Override
  String v();
}
