package names;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/** Exported as x and y; TypeScript sees them only here, since it doesn't see IgnoredNames. */
@JsType
public interface NamesFromIgnored extends IgnoredNames {
  @Override
  String foo();

  @Override
  String same();

  @JsMethod
  int other();
}
