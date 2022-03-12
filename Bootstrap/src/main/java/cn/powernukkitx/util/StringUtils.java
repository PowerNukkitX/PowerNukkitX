package cn.powernukkitx.util;

public final class StringUtils {
    public static String beforeLast(String str, String splitter) {
        final int i = str.indexOf(splitter);
        if(i == -1) return str;
        return str.substring(0, i);
    }
}
