package names;

import jsinterop.annotations.JsType;

/** Unannotated overrides in a JsType are exported as a and b. */
@JsType
public class Child1 extends Parent {
  @Override
  public void ma() {}

  @Override
  public void mb() {}
}
