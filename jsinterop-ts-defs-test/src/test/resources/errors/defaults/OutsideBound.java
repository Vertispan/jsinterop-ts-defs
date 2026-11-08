package errors.defaults;

import com.vertispan.tsdefs.annotations.TsDefault;
import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** Item is not a SpecialItem, so TypeScript would reject this default. */
@TsInterface
public interface OutsideBound<@TsDefault(Item.class) T extends SpecialItem> {
  @JsMethod
  T get();
}
