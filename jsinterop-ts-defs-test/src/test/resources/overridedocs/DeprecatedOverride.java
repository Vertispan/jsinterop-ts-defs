package overridedocs;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

@TsInterface
public interface DeprecatedOverride extends DocBase {
  @Deprecated
  @Override
  @JsMethod
  String name();
}
