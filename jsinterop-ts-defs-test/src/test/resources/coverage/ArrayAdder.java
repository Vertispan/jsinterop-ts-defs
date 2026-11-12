package coverage;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** An array where NativeRest takes a rest parameter, which TypeScript rejects. */
@TsInterface
public interface ArrayAdder extends NativeRest {
  @Override
  @JsMethod
  void add(String[] names);
}
