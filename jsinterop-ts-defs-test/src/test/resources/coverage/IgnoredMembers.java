package coverage;

import com.vertispan.tsdefs.annotations.TsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@TsIgnore
@JsType
public interface IgnoredMembers {
  @JsMethod
  String m();

  @JsProperty
  String getP();
}
