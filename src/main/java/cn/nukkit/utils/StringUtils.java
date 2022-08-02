package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public final class StringUtils {
    private StringUtils() {

    }

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

    @NotNull
    public static String capitalize(@NotNull String str) {
        if (str.length() == 0) {
            return "";
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
