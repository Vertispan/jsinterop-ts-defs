package warnings.overrides;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/** Both overrides are exported as x and y, not foo and bar. */
@JsType
public interface RenamedPairSub extends RenamedPair {
  @Override
  String foo();

  @Override
  PlainItem bar();

  @JsMethod
  int other();
}
