package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** JsInterop exports both overrides under Shape's names, with the narrower types. */
@TsInterface
public interface NarrowCircle extends Shape {
  @Override
  Double getSize();

  @Override
  String text();

  @JsMethod
  double radius();
}
