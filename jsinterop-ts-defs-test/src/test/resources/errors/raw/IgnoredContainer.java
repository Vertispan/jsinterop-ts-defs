package errors.raw;

import com.vertispan.tsdefs.annotations.TsIgnore;
import jsinterop.annotations.JsType;

/** Not emitted itself; its members are merged into subtypes. */
@TsIgnore
@JsType
public interface IgnoredContainer<T> {
  T get();
}
