#!/usr/bin/env bash
set -euo pipefail
gradle assembleDebug
echo "APK: app/build/outputs/apk/debug/app-debug.apk"
