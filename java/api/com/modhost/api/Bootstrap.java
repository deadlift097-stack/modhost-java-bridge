package com.modhost.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Entry points called from C++. Mods are described by a sidecar <name>.json next to <name>.jar. */
public final class Bootstrap {
    private static final List<ModHostMod> MODS = new ArrayList<ModHostMod>();
    private static final ModLogger LOG = new ModLogger("ModHost");
    private static final Pattern MAIN = Pattern.compile("\"main\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern ID = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");

    private Bootstrap() {}

    public static void loadMods(String modsDir, String dataRoot) {
        File[] metas = new File(modsDir).listFiles(new FilenameFilter() {
            public boolean accept(File d, String n) { return n.endsWith(".json"); }
        });
        if (metas == null) { LOG.warn("no mods dir: " + modsDir); return; }
        for (File meta : metas) {
            try {
                String json = read(meta);
                Matcher m = MAIN.matcher(json);
                if (!m.find()) { LOG.warn(meta.getName() + ": missing \"main\""); continue; }
                Matcher i = ID.matcher(json);
                String id = i.find() ? i.group(1) : meta.getName().replace(".json", "");
                Object o = Class.forName(m.group(1), true, Bootstrap.class.getClassLoader())
                        .getDeclaredConstructor().newInstance();
                if (!(o instanceof ModHostMod)) { LOG.warn(id + ": main is not a ModHostMod"); continue; }
                ModHostMod mod = (ModHostMod) o;
                File data = new File(dataRoot, id);
                data.mkdirs();
                mod.onLoad(new ModContext(id, data.getAbsolutePath()));
                MODS.add(mod);
                LOG.info("loaded " + id);
            } catch (Throwable t) {
                LOG.error("failed to load " + meta.getName() + ": " + t);
            }
        }
    }

    public static void enableAll() {
        for (ModHostMod m : MODS) {
            try { m.onEnable(); } catch (Throwable t) { LOG.error("onEnable: " + t); }
        }
    }

    public static void disableAll() {
        for (ModHostMod m : MODS) {
            try { m.onDisable(); } catch (Throwable t) { LOG.error("onDisable: " + t); }
        }
    }

    public static void tick() { Events.fireTick(); }

    private static String read(File f) throws Exception {
        FileInputStream in = new FileInputStream(f);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
            return out.toString("UTF-8");
        } finally { in.close(); }
    }
}
