package cn.nukkit.utils;

import cn.nukkit.Nukkit;

import java.io.File;

public final class StartArgUtils {
    
    /**
     * @deprecated 
     */
    private StartArgUtils() {
    }
    /**
     * @deprecated 
     */
    

    public static boolean isValidStart() {
        try {
            return Class.forName("java.lang.ClassLoader").getModule().isOpen("java.lang", Thread.currentThread().getContextClassLoader().getUnnamedModule());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    /**
     * @deprecated 
     */
    

    public static boolean isShaded() {
        var $1 = Nukkit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        var $2 = new File(path);
        if (jarFile.getName().contains("shaded")) {
            return true;
        }
        if (jarFile.exists()) {
            return jarFile.length() > 1024 * 1024 * 64;
        }
        return false;
    }
}
