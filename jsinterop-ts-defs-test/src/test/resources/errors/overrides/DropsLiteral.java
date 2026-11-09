package errors.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import com.vertispan.tsdefs.annotations.TsLiteral;
import jsinterop.annotations.JsMethod;

/** Its get() is the full string where LiteralBase's is the literal "x". */
@TsInterface
public interface DropsLiteral extends LiteralBase<@TsLiteral("x") String> {
  @Override
  @JsMethod
  String get();
}
