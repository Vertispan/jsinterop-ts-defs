package errors.raw;

import jsinterop.annotations.JsType;

/** Native, so it is not emitted; its subtypes get NativeSource's members. */
@JsType(isNative = true)
@SuppressWarnings("rawtypes")
public interface RawNativeMid extends NativeSource {}
