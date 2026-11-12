package errors.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import com.vertispan.tsdefs.annotations.TsLiteral;
import jsinterop.annotations.JsMethod;

@TsInterface
public interface NumberLiteral {
  @JsMethod
  @TsLiteral("1")
  double one();
}
