package overridedocs;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

@TsInterface
public interface DocBase {
  @JsMethod
  String name();

  @Deprecated
  @JsMethod
  String old();
}
