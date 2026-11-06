package types.functions;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;

@JsType
public abstract class UsesGenericMethodFunction {

  public abstract void take(GenericMethodFn<String> fn);

  public abstract void takeAgain(GenericMethodFn<String> fn);

  @JsFunction
  public interface GenericMethodFn<T> {
    <T> T apply(T t);
  }
}
