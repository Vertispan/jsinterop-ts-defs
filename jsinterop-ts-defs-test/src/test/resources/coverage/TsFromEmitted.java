package coverage;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** Extends EmittedMembers in TypeScript too, so its members aren't repeated here. */
@TsInterface
public interface TsFromEmitted extends EmittedMembers {
  @JsMethod
  int other();
}
