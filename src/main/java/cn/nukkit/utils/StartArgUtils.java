package cn.nukkit.utils;

public final class StartArgUtils {
    private StartArgUtils() {
    }

    public static boolean isValidStart() {
        try {
            return Class.forName("java.lang.ClassLoader").getModule().isOpen("java.lang", Thread.currentThread().getContextClassLoader().getUnnamedModule());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
