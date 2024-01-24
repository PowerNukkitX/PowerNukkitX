#!/bin/bash

JAR_NAME="powernukkitx"
LIB_PATH="./libs"
GRAAL_SDK="graal-sdk-23.0.0.jar"
TRUFFLE_API="truffle-api-23.0.0.jar"

java -Dfile.encoding=UTF-8 \
    -Djansi.passthrough=true \
    -Dterminal.ansi=true \
    -XX:+UnlockExperimentalVMOptions \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+EnableJVMCI \
    --module-path="$LIB_PATH/$GRAAL_SDK:$LIB_PATH/$TRUFFLE_API" \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.io=ALL-UNNAMED \
    -cp "$JAR_NAME.jar:$LIB_PATH/*" \
    cn.nukkit.JarStart
