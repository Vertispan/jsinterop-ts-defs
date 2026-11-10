package coverage;

import com.vertispan.tsdefs.annotations.TsIgnore;
import jsinterop.annotations.JsType;

@TsIgnore
@JsType
public interface IgnoredGetter {
  Foo get();
}
