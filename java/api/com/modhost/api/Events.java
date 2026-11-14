package com.modhost.api;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Event hub. Native hooks call into this through Bootstrap. */
public final class Events {
    private static final List<Runnable> TICK = new CopyOnWriteArrayList<Runnable>();
    private Events() {}

    public static void onTick(Runnable r) { TICK.add(r); }

    static void fireTick() {
        for (Runnable r : TICK) {
            try { r.run(); }
            catch (Throwable t) { new ModLogger("ModHost").error("tick handler failed: " + t); }
        }
    }
}
