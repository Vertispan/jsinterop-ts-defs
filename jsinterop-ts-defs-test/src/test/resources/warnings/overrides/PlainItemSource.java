package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** Each override is legal Java but would not extend ItemSource in TypeScript. */
@TsInterface
public interface PlainItemSource extends ItemSource {
  @Override
  @JsMethod
  PlainItem one();

  @Override
  @JsMethod
  PlainItem[] all();

  @Override
  @JsMethod
  void add(String[] names);

  @JsMethod
  int other();
}
