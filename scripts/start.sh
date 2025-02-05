#!/bin/bash

echo "PowerNukkitX server software for Minecraft: Bedrock Edition"

# Function to print error messages and exit
print_error_and_exit() {
    echo "$1"
    exit 1
}

# Check if Java is installed
if ! java -version &> /dev/null; then
    print_error_and_exit "Java is not installed. Please install Java 21 or higher. Refer to https://docs-pnx.pages.dev/requirements"
fi

# Check if powernukkitx.jar exists
if [ ! -f "powernukkitx.jar" ]; then
    print_error_and_exit "PowerNukkitX.jar not found. Download it from https://github.com/PowerNukkitX/PowerNukkitX/releases"
fi

# Check if libs directory exists
if [ ! -d "libs" ]; then
    print_error_and_exit "The libs directory was not found. Ensure the directory is in the current directory. Download it from https://github.com/PowerNukkitX/PowerNukkitX/releases"
fi

JAVA_CMD="java"
JAR_NAME="powernukkitx.jar"

$JAVA_CMD -Dfile.encoding=UTF-8 \
    -Djansi.passthrough=true \
    -Dterminal.ansi=true \
    -XX:+UseZGC \
    -XX:+ZGenerational \
    -XX:+UseStringDeduplication \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.io=ALL-UNNAMED \
    --add-opens java.base/java.net=ALL-UNNAMED \
    -cp $JAR_NAME:./libs/* \
    cn.nukkit.Nukkit