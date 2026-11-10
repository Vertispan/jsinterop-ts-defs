package errors.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Any;

@TsInterface
public interface AnyBase {
  @JsMethod
  Any a();

  @JsMethod
  PlainThing u();
}
