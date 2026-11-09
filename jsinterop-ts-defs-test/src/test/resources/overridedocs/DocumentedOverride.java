package overridedocs;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

@TsInterface
public interface DocumentedOverride extends DocBase {
  /** Documented here. */
  @Override
  @JsMethod
  String name();
}
