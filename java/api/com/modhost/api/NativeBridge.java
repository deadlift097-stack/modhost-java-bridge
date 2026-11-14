package com.modhost.api;

/** Natives are registered from C++ (JavaBridge.cpp) via RegisterNatives. */
public final class NativeBridge {
    private NativeBridge() {}
    public static native void log(int level, String message);
}
