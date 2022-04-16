package cn.powernukkitx.bootstrap.info.locator;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GraalModuleLocator extends Locator<Void> {
    @Override
    public List<Location<Void>> locate() {
        final File libsDir = new File("./libs");
        if (libsDir.exists() && libsDir.isDirectory()) {
            File[] files = libsDir.listFiles();
            if (files == null) {
                return Collections.emptyList();
            }
            return Arrays.stream(files).filter(file -> (file.getName().startsWith("graal-sdk") && file.getName().endsWith(".jar")) || (file.getName().startsWith("truffle-api") && file.getName().endsWith(".jar")))
                    .map(file -> new Location<Void>(file, null)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
