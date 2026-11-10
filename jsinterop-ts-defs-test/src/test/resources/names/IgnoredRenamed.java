package names;

import com.vertispan.tsdefs.annotations.TsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/** foo() is exported as bar; @TsIgnore only keeps it out of TypeScript. */
@JsType
public class IgnoredRenamed {
  @TsIgnore
  @JsMethod(name = "bar")
  public void foo() {}

  public void other() {}
}
