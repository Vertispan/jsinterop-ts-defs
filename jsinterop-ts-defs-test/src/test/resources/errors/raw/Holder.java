package errors.raw;

import jsinterop.annotations.JsType;

public class Holder {
  @JsType
  public interface Inner<T> {
    T get();
  }
}
