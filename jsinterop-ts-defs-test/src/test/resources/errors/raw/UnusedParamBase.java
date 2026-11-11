package errors.raw;

import com.vertispan.tsdefs.annotations.TsIgnore;
import jsinterop.annotations.JsType;

/** Merged into subclasses; no exported member uses T. */
@TsIgnore
@JsType
public class UnusedParamBase<T> {
  public String name;

  private T cache;
}
