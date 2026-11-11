package coverage;

import com.vertispan.tsdefs.annotations.TsInterface;
import com.vertispan.tsdefs.annotations.TsLiteral;
import jsinterop.annotations.JsMethod;

/** Narrows status() to the string literal "unknown", not to TypeScript's unknown. */
@TsInterface
public interface UnknownStatus extends StatusSource {
  @Override
  @JsMethod
  @TsLiteral("unknown")
  String status();
}
