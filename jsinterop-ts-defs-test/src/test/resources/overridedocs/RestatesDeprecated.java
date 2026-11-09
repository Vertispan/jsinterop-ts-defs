package overridedocs;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

@TsInterface
public interface RestatesDeprecated extends DocBase {
  @Override
  @JsMethod
  String old();

  @JsMethod
  String other();
}
