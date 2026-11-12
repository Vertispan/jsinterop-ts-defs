package coverage;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsNullable;

/** Also takes null, which TypeScript accepts in an override, since method parameters are bivariant. */
@TsInterface
public interface NullTaker extends Taker {
  /** Takes a string or null. */
  @Override
  @JsMethod
  void take(@JsNullable String s);
}
