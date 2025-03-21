package cn.nukkit.utils;

import cn.nukkit.Nukkit;

import java.io.File;

public final class StartArgUtils {
    private StartArgUtils() {
    }

    public static boolean loadModules() {
        Module module = Nukkit.class.getModule();
        Module targetModule = Object.class.getModule(); // java.base module
        module.addOpens("java.lang", targetModule);
        module.addOpens("java.io", targetModule);
        module.addOpens("java.net", targetModule);

        return true;
    }

    public static boolean isShaded() {
        var path = Nukkit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        var jarFile = new File(path);
        if (jarFile.getName().contains("shaded")) {
            return true;
        }
        if (jarFile.exists()) {
            return jarFile.length() > 1024 * 1024 * 64;
        }
        return false;
    }
}
