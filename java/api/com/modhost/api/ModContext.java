package com.modhost.api;

public final class ModContext {
    public final String id;
    public final String dataDir;
    public final ModLogger logger;

    public ModContext(String id, String dataDir) {
        this.id = id;
        this.dataDir = dataDir;
        this.logger = new ModLogger(id);
    }
}
