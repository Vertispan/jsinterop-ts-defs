package errors.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

@TsInterface
public interface LiteralBase<T> {
  @JsMethod
  T get();
}
