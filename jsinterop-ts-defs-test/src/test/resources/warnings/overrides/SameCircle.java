package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** The same types as Shape's, so TypeScript loses nothing. */
@TsInterface
public interface SameCircle extends Shape {
  @Override
  Object getSize();

  @Override
  Object text();

  @JsMethod
  double radius();
}
