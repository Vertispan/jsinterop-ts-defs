package errors.access;

import com.vertispan.tsdefs.annotations.TsInterface;
import jsinterop.annotations.JsType;

@TsInterface
@JsType
public class JsTypeInterfaceWithPrivateStaticMember {

  private static String property = "INTERFACE STATIC PROPERTY";
}