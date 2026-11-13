package copieddocs;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsMethod;

@TsInterface
public interface DocsView extends DocsSource {
  @JsMethod
  int other();
}
