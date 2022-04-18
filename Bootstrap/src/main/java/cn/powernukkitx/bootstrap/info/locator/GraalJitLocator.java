package cn.powernukkitx.bootstrap.info.locator;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GraalJitLocator extends Locator<String> {

    @Override
    public List<Location<String>> locate() {
        final File graalJITDir = new File("./components/graaljit");
        if (graalJITDir.exists() && graalJITDir.isDirectory()) {
            GraalJITLocation compiler = null;
            GraalJITLocation management = null;
            final File[] files = graalJITDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if(files == null) {
                return Collections.emptyList();
            }
            for (File file : files) {
                if (file.getName().startsWith("compiler-management")) {
                    management = new GraalJITLocation(file, file.getName().replace("compiler-management-", "").replace(".jar", ""));
                } else if (file.getName().startsWith("compiler")) {
                    compiler = new GraalJITLocation(file, file.getName().replace("compiler-", "").replace(".jar", ""));
                }
            }
            return Arrays.asList(compiler, management);
        } else {
            return Collections.emptyList();
        }
    }

    public static class GraalJITLocation extends Location<String> {
        public GraalJITLocation(File file, String info) {
            super(file, info);
        }
    }
}
