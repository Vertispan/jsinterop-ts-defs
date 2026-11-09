package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** Restates NarrowedText.text(), whose narrower type TypeScript already sees. */
@TsInterface
public interface RestatedText extends NarrowedText {
  @Override
  String text();

  @JsMethod
  double width();
}
