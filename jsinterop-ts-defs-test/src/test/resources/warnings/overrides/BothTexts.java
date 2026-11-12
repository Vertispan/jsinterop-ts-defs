package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** Reaches RenamedText directly and through NarrowedText, which already emits String. */
@TsInterface
public interface BothTexts extends RenamedText, NarrowedText {
  @Override
  String text();

  @JsMethod
  double width();
}
