package types.functions;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;

@JsType
public class UsesIFunction {

    public void doSomething(IFunction<String> arg){

    }

    public void doSomethingWithObject(Object obj){

    }

    @JsFunction
    public static interface IFunction<T> {
        void doSomething(T arg);
    }
}
