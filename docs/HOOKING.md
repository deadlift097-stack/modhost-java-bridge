# Hooking game events into Java

`modhost::bridge().tick()` is ready but nothing calls it yet. Find one Bedrock function that runs
once per game tick, hook it with the template's `pl/memory/Hook.hpp` + `Signature.hpp`,
and call `modhost::bridge().tick()` from the detour. Signatures change per Minecraft version
(1.26.62.1 etc.), so keep them in one file and update them on each game update.

More events work the same way: C++ detour -> new static method in `Bootstrap`/`Events` -> bridge call.
