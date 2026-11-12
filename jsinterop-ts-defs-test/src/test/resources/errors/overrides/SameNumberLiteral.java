package errors.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import com.vertispan.tsdefs.annotations.TsLiteral;
import jsinterop.annotations.JsMethod;

/** The same number as NumberLiteral's, written differently. */
@TsInterface
public interface SameNumberLiteral extends NumberLiteral {
  @Override
  @JsMethod
  @TsLiteral("1.0")
  double one();
}
