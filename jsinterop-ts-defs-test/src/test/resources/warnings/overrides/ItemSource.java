package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

@TsInterface
public interface ItemSource {
  @JsMethod
  Item one();

  @JsMethod
  Item[] all();

  @JsMethod
  void add(String... names);
}
