package types.functions;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;

@JsType
public abstract class UsesGenericMethodFunction {

  public abstract void take(GenericMethodFn fn);

  public abstract void takeAgain(GenericMethodFn fn);

  @JsFunction
  public interface GenericMethodFn {
    <U> U apply(U u);
  }
}
