package names;

import jsinterop.annotations.JsMethod;

/** As in GWT's JsInteropRestrictionCheckerTest.testOverrideNoNameSucceeds. */
public class Parent {
  @JsMethod(name = "a")
  public void ma() {}

  @JsMethod(name = "b")
  public void mb() {}
}
