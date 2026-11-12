package names;

import jsinterop.annotations.JsMethod;

/** A @JsMethod without a name keeps the overridden method's name, a and b. */
public class Child2 extends Parent {
  @Override
  @JsMethod
  public void ma() {}

  @Override
  @JsMethod
  public void mb() {}
}
