# ModHost Java Bridge (LeviLauncher Android)

Runs Java mods inside Minecraft Bedrock on Android through a native LeviLauncher mod.
This is **not** Fabric/NeoForge: mods target the small API in `java/api` (`ModHostMod`, `Events`, `ModLogger`).
Java Edition mods must be ported to it.

    C++ mod (template) -> JavaBridge -> DexClassLoader -> com.modhost.api.Bootstrap -> your ModHostMod

## Setup
1. Create a repo from https://github.com/QYCottage/levilauncher-android-mod-template ("Use this template").
2. Copy in `src/bridge`, `src/mod/MyMod.snippet.cpp` (merge into `MyMod.cpp`), `cmake/`,
   `scripts/build-java.sh`, `java/`, `docs/`, `.github/workflows/build.yml`.
3. In `CMakeLists.txt`: `include(cmake/modhost.cmake)` then `modhost_attach(<mod target>)`.
4. Make `scripts/package.ps1` put `build/java/modhost-api.dex.jar` into the `.levipack` resources
   (see how it stages `config.json` and add the file beside it).
5. Use `-DMOD_MINECRAFT_VERSIONS='["1.26.*"]'`.
6. Push. Actions builds the dex files, then the `.levipack`.

## Writing a mod
Implement `ModHostMod`, build with javac + d8 (see `scripts/build-java.sh`), and put
`yourmod.jar` + `yourmod.json` (`{"id": "...", "main": "pkg.Class"}`) into `<dataDir>/mods`.
Jars must be dexed with d8; Android can't load plain class-file jars.

## Status
- Done: JVM discovery, class loading, API, lifecycle, logging, hello mod, CI.
- Not done: game event hooks (need Bedrock signatures per version, see `docs/HOOKING.md`), Lua loader.
- Untested on a device or inside the real template; expect small fixes.
