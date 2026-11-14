#!/usr/bin/env python3
"""Overlay the Java bridge onto a checkout of the LeviLauncher mod template.
usage: overlay.py <bridge-repo> <template-checkout>"""
import pathlib
import shutil
import sys

bridge, tpl = (pathlib.Path(a) for a in sys.argv[1:3])

shutil.copytree(bridge / "src" / "bridge", tpl / "src" / "bridge", dirs_exist_ok=True)
shutil.copytree(bridge / "cmake", tpl / "cmake", dirs_exist_ok=True)

res = tpl / "resources"
res.mkdir(exist_ok=True)
dex = bridge / "build" / "java" / "modhost-api.dex.jar"
if dex.exists():
    shutil.copy(dex, res / dex.name)

# CMake: compile the bridge into the mod library
cm = tpl / "CMakeLists.txt"
cm.write_text(cm.read_text() + "\ninclude(cmake/modhost.cmake)\nmodhost_attach(${MOD_LIBRARY_NAME})\n")

# C++: call the bridge from the mod lifecycle
p = tpl / "src" / "mod" / "MyMod.cpp"
s = p.read_text()


def rep(old, new):
    global s
    if old not in s:
        sys.exit("patch anchor missing in MyMod.cpp: " + old)
    s = s.replace(old, new, 1)


LOAD = '''{
        namespace fs = std::filesystem;
        auto& self = getSelf();
        fs::path res  = fs::path(self.getResourceDir());
        fs::path data = fs::path(self.getDataDir());
        std::error_code ec;
        fs::create_directories(data / "mods", ec);
        modhost::bridge().init(res / "modhost-api.dex.jar", data / "mods", data / "data", nullptr);
    }'''

rep('getSelf().getLogger().debug("Loading...");', LOAD)
rep('getSelf().getLogger().debug("Enabling...");', "modhost::bridge().enable();")
rep('getSelf().getLogger().debug("Disabling...");', "modhost::bridge().disable();")
rep('getSelf().getLogger().debug("Unloading...");', "modhost::bridge().shutdown();")
p.write_text('#include "bridge/JavaBridge.h"\n#include <filesystem>\n' + s)
print("overlay ok")
