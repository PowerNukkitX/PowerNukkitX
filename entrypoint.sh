#!/bin/bash
sed -i '/.* language: "eng"*/c\ language: "'$language'"' nukkit.yml
echo -e "\n\n\n\n\n" > /pnx/pnx-cli-config.ini
if [ $language = "eng" ]; then sed -i '1c language=en-us' /pnx/pnx-cli-config.ini; fi
if [ $language = "chs" ]; then sed -i '1c language=zh-cn' /pnx/pnx-cli-config.ini; fi
sed -i '2c vmMemory='$memory /pnx/pnx-cli-config.ini
java -jar cli.jar