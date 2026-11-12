package coverage;

import jsinterop.annotations.JsNullable;
import jsinterop.annotations.JsType;

/** Covers IgnoredGetter.get(), which TypeScript doesn't see. */
@JsType
public interface NullableGetter extends IgnoredGetter {
  @Override
  @JsNullable
  Foo get();
}
