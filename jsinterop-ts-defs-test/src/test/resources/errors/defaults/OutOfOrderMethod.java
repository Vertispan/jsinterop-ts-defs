package errors.defaults;

import com.vertispan.tsdefs.annotations.TsDefault;
import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** The same ordering rule on a method's type parameters. */
@TsInterface
public interface OutOfOrderMethod {
  @JsMethod
  <@TsDefault(Item.class) A, B> A pick(B b);
}
