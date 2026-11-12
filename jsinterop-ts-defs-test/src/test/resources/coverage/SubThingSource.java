package coverage;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

/** Narrows get() between emitted types whose namespaces are object and unknown. */
@TsInterface
public interface SubThingSource extends ThingSource {
  @Override
  @JsMethod
  ObjectSpaceSubThing get();

  @JsMethod
  int other();
}
