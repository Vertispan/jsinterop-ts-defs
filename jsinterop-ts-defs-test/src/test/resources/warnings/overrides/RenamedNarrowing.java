package warnings.overrides;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/** Public in a JsType, so exported, but under RenamedSource's name x, not foo. */
@TsInterface
@JsType
public interface RenamedNarrowing extends RenamedSource {
  @Override
  RenamedNarrowing foo();

  @JsMethod
  int other();
}
