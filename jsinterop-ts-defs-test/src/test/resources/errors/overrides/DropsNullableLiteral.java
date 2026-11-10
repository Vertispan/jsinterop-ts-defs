package errors.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsNullable;

/** Its na() is any string or null, where NullableLiteral's is "a" or null. */
@TsInterface
public interface DropsNullableLiteral extends NullableLiteral {
  @Override
  @JsMethod
  @JsNullable
  String na();
}
