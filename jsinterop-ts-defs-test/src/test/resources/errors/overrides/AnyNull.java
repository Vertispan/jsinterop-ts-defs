package errors.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsNullable;
import jsinterop.base.Any;

/** any and unknown already take null and undefined, so these don't widen them. */
@TsInterface
public interface AnyNull extends AnyBase {
  @Override
  @JsMethod
  @JsNullable
  Any a();

  @Override
  @JsMethod
  @JsNullable
  PlainThing u();
}
