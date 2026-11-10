package errors.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import com.vertispan.tsdefs.annotations.TsLiteral;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsNullable;

@TsInterface
public interface NullableLiteral {
  @JsMethod
  @JsNullable
  @TsLiteral("a")
  String na();
}
