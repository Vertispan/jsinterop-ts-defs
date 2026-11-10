package names;

import com.vertispan.tsdefs.annotations.TsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@TsIgnore
@JsType
public interface IgnoredNames {
  @JsMethod(name = "x")
  Object foo();

  @JsMethod(name = "y")
  String same();
}
