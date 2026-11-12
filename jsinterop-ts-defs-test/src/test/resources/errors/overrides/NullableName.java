package errors.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsNullable;

/** Widens NameBase.name() to nullable, which TypeScript rejects in an override. */
@TsInterface
public interface NullableName extends NameBase {
  @Override
  @JsMethod
  @JsNullable
  String name();
}
