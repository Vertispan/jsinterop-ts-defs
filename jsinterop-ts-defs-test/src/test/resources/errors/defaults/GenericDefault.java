package errors.defaults;

import com.vertispan.tsdefs.annotations.TsDefault;
import jsinterop.annotations.JsType;

@JsType
public interface GenericDefault<@TsDefault(GenericBox.class) T> {
  T get();
}
