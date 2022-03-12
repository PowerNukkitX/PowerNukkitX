package cn.powernukkitx.bootstrap.util;

public final class StringUtils {
    public static String beforeLast(String str, String splitter) {
        final int i = str.indexOf(splitter);
        if (i == -1) return str;
        return str.substring(0, i);
    }

    public static String displayableBytes(long bytes) {
        if (bytes >= 1024 * 1024 * 2) {
            return String.format("%.2fMB", bytes / 1024.0 / 1024);
        } else if (bytes >= 1024 * 2) {
            return String.format("%.2fKB", bytes / 1024.0);
        } else {
            return String.format("%dB", bytes);
        }
    }

    public static int getPrintLength(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;

        }
        return length;
    }

    public static String repeat(String s, int time) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < time; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
