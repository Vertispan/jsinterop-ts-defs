package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;

/** Annotated as the warning suggests, so the narrower types are emitted. */
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
