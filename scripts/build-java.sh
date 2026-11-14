#!/usr/bin/env bash
# Builds the Java API + hello mod and converts them to Android dex (d8).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="$ROOT/build/java"
SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
[ -n "$SDK" ] || { echo "set ANDROID_HOME"; exit 1; }
D8="$(ls -d "$SDK"/build-tools/*/d8 | sort -V | tail -1)"
ANDROID_JAR="$(ls -d "$SDK"/platforms/android-*/android.jar | sort -V | tail -1)"

rm -rf "$OUT"; mkdir -p "$OUT/api-classes" "$OUT/hello-classes" "$OUT/mods"
javac --release 8 -d "$OUT/api-classes" $(find "$ROOT/java/api" -name '*.java')
jar cf "$OUT/api.jar" -C "$OUT/api-classes" .
javac --release 8 -cp "$OUT/api.jar" -d "$OUT/hello-classes" $(find "$ROOT/java/hello-java-mod" -name '*.java')
jar cf "$OUT/hello.class.jar" -C "$OUT/hello-classes" .

"$D8" --release --min-api 26 --lib "$ANDROID_JAR" --output "$OUT/modhost-api.dex.jar" "$OUT/api.jar"
"$D8" --release --min-api 26 --lib "$ANDROID_JAR" --classpath "$OUT/api.jar" \
      --output "$OUT/mods/hello-java-mod.jar" "$OUT/hello.class.jar"
cp "$ROOT/java/hello-java-mod/hello-java-mod.json" "$OUT/mods/"
echo "done: $OUT"
