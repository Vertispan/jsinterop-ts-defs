package coverage;

import jsinterop.annotations.JsType;

/** Restates Converter.conv() with its own type variable name and docs. */
@JsType
public interface DocumentedConverter extends Converter {
  /** Converts nothing. */
  @Override
  <Y> Y conv(Y y);
}
