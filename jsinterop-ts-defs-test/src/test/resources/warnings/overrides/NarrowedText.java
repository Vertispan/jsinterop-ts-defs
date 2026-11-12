package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** Already emits the narrower type under the JS name. */
@TsInterface
public interface NarrowedText extends RenamedText {
  @Override
  @JsMethod(name = "d")
  String text();
}
