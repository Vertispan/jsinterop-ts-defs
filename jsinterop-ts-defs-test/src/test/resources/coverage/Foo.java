package coverage;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

@TsInterface
public interface Foo {
  @JsMethod
  String foo();
}
