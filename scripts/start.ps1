# Check if Java is installed
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "Java is not installed. Please install Java 21 or higher."
    Write-Host "You can refer to the installation instructions at https://docs-pnx.pages.dev/requirements"
    exit 1
}

# Check if powernukkitx.jar exists
if (-not (Test-Path -Path "powernukkitx.jar")) {
    Write-Host "PowerNukkitX.jar not found"
    Write-Host "You can download the file from https://github.com/PowerNukkitX/PowerNukkitX/releases"
    exit 1
}

# Check if libs directory exists
if (-not (Test-Path -Path "libs" -PathType Container)) {
    Write-Host "The libs directory was not found. Please ensure the directory is in the current directory."
    Write-Host "You can download the directory from https://github.com/PowerNukkitX/PowerNukkitX/releases"
    exit 1
}

$JAVA_CMD = "java"
$JAR_NAME = "powernukkitx.jar"

& $JAVA_CMD -Dfile.encoding=UTF-8 `
    -Djansi.passthrough=true `
    -Dterminal.ansi=true `
    -XX:+UseZGC `
    -XX:+ZGenerational `
    -XX:+UseStringDeduplication `
    --add-opens java.base/java.lang=ALL-UNNAMED `
    --add-opens java.base/java.io=ALL-UNNAMED `
    --add-opens java.base/java.net=ALL-UNNAMED `
    -cp "$JAR_NAME;./libs/*" `
    cn.nukkit.Nukkit