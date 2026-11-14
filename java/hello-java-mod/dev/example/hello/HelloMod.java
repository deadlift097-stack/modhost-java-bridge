package dev.example.hello;

import com.modhost.api.Events;
import com.modhost.api.ModContext;
import com.modhost.api.ModHostMod;

public class HelloMod implements ModHostMod {
    private ModContext ctx;
    private int ticks;

    public void onLoad(ModContext ctx) {
        this.ctx = ctx;
        ctx.logger.info("Hello from Java! data dir: " + ctx.dataDir);
        Events.onTick(new Runnable() {
            public void run() {
                if (++ticks % 1200 == 0) HelloMod.this.ctx.logger.info("1200 ticks passed");
            }
        });
    }

    public void onEnable()  { ctx.logger.info("enabled"); }
    public void onDisable() { ctx.logger.info("disabled"); }
}
