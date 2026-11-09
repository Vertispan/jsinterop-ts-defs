package errors.raw;

/** Not a JsType and no JS members: nothing of it reaches TypeScript. */
public class PlainGeneric<T> {
  public T value;
}
