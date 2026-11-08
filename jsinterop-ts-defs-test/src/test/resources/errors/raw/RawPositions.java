package errors.raw;

import jsinterop.annotations.JsType;

@JsType
@SuppressWarnings("rawtypes")
public class RawPositions<T extends Container> {
  public Container field;

  public RawPositions(Container ctorParam) {}

  public <U extends Container> void generic(U u) {}

  @JsType
  public static class Nested {
    public Container nestedField;
  }
}
