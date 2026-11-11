package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/**
 * JsInterop exports both overrides under Shape's names, size and describe. TypeScript gets the
 * narrower size; describe's object can't be narrowed to string, so Shape's stands. TODO(#144)
 */
@TsInterface
public interface NarrowCircle extends Shape {
  @Override
  Double getSize();

  @Override
  String text();

  @JsMethod
  double radius();
}
