#!/bin/bash

# Construct the full path to the Java executable
JAVA_CMD="java"
JAR_NAME="powernukkitx.jar"

# Execute the Java command
"$JAVA_CMD" -Dfile.encoding=UTF-8 \
    -Djansi.passthrough=true \
    -Dterminal.ansi=true \
    -XX:+UnlockExperimentalVMOptions \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+EnableJVMCI \
    --module-path="./libs/graal-sdk-23.0.0.jar:./libs/truffle-api-23.0.0.jar" \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.io=ALL-UNNAMED \
    -cp "$JAR_NAME:./libs/*" \
    cn.nukkit.Nukkit