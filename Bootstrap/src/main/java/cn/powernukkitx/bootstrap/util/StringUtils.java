package cn.powernukkitx.bootstrap.util;

import java.net.URL;

public final class StringUtils {
    public static String beforeLast(String str, String splitter) {
        final int i = str.indexOf(splitter);
        if (i == -1) return str;
        return str.substring(0, i);
    }

    public static String afterFirst(String str, String splitter) {
        final int i = str.indexOf(splitter);
        if (i == -1) return str;
        return str.substring(i + 1);
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

    public static String displayableFreq(long hz) {
        if (hz >= 1000000000) {
            return String.format("%.2fGHz", hz / 1000000000.0);
        } else if (hz >= 1000 * 1000) {
            return String.format("%.2fMHz", hz / 1000000.0);
        } else if (hz >= 1000) {
            return String.format("%.2fKHz", hz / 1000.0);
        } else {
            return String.format("%dHz", hz);
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

    public static String uriSuffix(String s) {
        final int splashIndex = s.lastIndexOf("/");
        String last;
        if (splashIndex == -1) {
            last = s;
        } else {
            last = s.substring(splashIndex + 1);
        }
        final int dotIndex = last.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        } else {
            return last.substring(dotIndex + 1);
        }
    }

    public static String uriSuffix(URL url) {
        return uriSuffix(url.toString());
    }

    public static String stripTrailing(String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return str.substring(0, i + 1);
            }
        }
        return str;
    }
}
