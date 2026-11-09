package errors.raw;

import com.vertispan.tsdefs.annotations.TsIgnore;
import jsinterop.annotations.JsType;

/** Not emitted itself; its members are merged into subclasses. */
@TsIgnore
@JsType
public class IgnoredGenericBase<T> {
  public T held;
}
