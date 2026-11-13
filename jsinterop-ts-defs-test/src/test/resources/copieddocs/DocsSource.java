package copieddocs;

import com.vertispan.tsdefs.annotations.TsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;

@TsIgnore
public interface DocsSource {
  /** Doc for size. */
  @JsProperty
  double getSize();

  /** Doc for close. */
  @JsMethod
  void close();
}
