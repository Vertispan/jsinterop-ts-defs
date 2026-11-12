package names;

import com.vertispan.tsdefs.annotations.TsIgnore;
import jsinterop.annotations.JsType;

/** Its foo() is still exported as RenamedApi's bar. */
@JsType
public class IgnoredMiddle implements RenamedApi {
  @TsIgnore
  @Override
  public void foo() {}

  public void other() {}
}
