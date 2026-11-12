package coverage;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

@TsInterface
public interface StatusSource {
  @JsMethod
  String status();
}
