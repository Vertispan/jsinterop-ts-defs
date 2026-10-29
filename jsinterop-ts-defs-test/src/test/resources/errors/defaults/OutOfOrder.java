package errors.defaults;

import com.vertispan.tsdefs.annotations.TsDefault;
import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** B follows a defaulted parameter without a default of its own. */
@TsInterface
public interface OutOfOrder<@TsDefault(Item.class) A, B> {
  @JsMethod
  A a();

  @JsMethod
  B b();
}
