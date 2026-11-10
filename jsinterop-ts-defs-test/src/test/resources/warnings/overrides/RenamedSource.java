package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@TsInterface
@JsType
public interface RenamedSource {
  @JsMethod(name = "x")
  RenamedSource foo();
}
