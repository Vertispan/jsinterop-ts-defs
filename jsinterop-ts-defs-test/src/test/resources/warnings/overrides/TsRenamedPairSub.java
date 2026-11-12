package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/** As RenamedPairSub, in a TsInterface. */
@TsInterface
@JsType
public interface TsRenamedPairSub extends RenamedPair {
  @Override
  String foo();

  @Override
  PlainItem bar();

  @JsMethod
  int other();
}
