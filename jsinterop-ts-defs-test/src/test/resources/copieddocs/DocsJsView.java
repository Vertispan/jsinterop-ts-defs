package copieddocs;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType
public interface DocsJsView extends DocsSource {
  @JsMethod
  int other();
}
