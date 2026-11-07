package errors.raw;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsType;

@TsInterface
@JsType
public class GenericTsInterfaceBase<T> {
  public T value;
}
