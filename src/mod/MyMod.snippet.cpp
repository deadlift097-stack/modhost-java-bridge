// Merge these lines into the template's src/mod/MyMod.cpp (this is not a full file).
#include "bridge/JavaBridge.h"
#include <filesystem>

static void modhostLog(int level, const std::string& m) {
    // optional: forward to the loader logger; Android logcat already gets every line
    (void)level; (void)m;
}

bool ClangeMeMod::load() {
    namespace fs = std::filesystem;
    auto& self = getSelf();
    fs::path res  = fs::path(self.getResourceDir());
    fs::path data = fs::path(self.getDataDir());
    fs::create_directories(data / "mods");
    // users drop dexed .jar + .json into <dataDir>/mods ; the API dex ships in the .levipack resources
    return modhost::bridge().init(res / "modhost-api.dex.jar", data / "mods", data / "data", modhostLog);
}

bool ClangeMeMod::enable()  { modhost::bridge().enable();   return true; }
bool ClangeMeMod::disable() { modhost::bridge().disable();  return true; }
bool ClangeMeMod::unload()  { modhost::bridge().shutdown(); return true; }
