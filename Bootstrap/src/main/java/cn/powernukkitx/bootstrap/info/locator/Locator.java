package cn.powernukkitx.bootstrap.info.locator;

import java.util.List;

public abstract class Locator<T> {
    public abstract List<Location<T>> locate();

    public static String platformSuffix() {
        if (System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS")) {
            return ".exe";
        } else {
            return "";
        }
    }

    public static String platformSplitter() {
        if (System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS")) {
            return ";";
        } else {
            return ":";
        }
    }
}
