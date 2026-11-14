package com.modhost.api;

public final class ModLogger {
    public static final int DEBUG = 0, INFO = 1, WARN = 2, ERROR = 3;
    private final String prefix;

    public ModLogger(String prefix) { this.prefix = prefix; }

    public void debug(String m) { emit(DEBUG, m); }
    public void info(String m)  { emit(INFO, m); }
    public void warn(String m)  { emit(WARN, m); }
    public void error(String m) { emit(ERROR, m); }

    private void emit(int level, String m) {
        String line = "[" + prefix + "] " + m;
        try {
            NativeBridge.log(level, line);
        } catch (UnsatisfiedLinkError e) {
            System.out.println(line); // running outside the game (tests)
        }
    }
}
