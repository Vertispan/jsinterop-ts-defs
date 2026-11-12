package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;

/** Restates Shape's JS names explicitly; the same as NarrowCircle, which inherits them. */
@TsInterface
public interface AnnotatedCircle extends Shape {
  @Override
  @JsProperty(name = "size")
  Double getSize();

  @Override
  @JsMethod(name = "describe")
  String text();

  @JsMethod
  double radius();
}
