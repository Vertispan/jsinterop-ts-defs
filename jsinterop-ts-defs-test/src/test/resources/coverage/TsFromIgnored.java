package coverage;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** TypeScript doesn't see IgnoredMembers, so its members are declared here. */
@TsInterface
public interface TsFromIgnored extends IgnoredMembers {
  @JsMethod
  int other();
}
