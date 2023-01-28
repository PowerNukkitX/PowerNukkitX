package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    @Since("1.19.60-r1")
    public static List<String> fastSplit(String delimiter, String str) {
        return fastSplit(delimiter, str, Integer.MAX_VALUE);
    }

    @Since("1.19.60-r1")
    public static List<String> fastSplit(String delimiter, String str, int limit) {
        var tmp = str;
        var results = new ArrayList<String>();
        var count = 1;
        while(true){
            int j = tmp.indexOf(delimiter);
            if(j < 0) {
                results.add(tmp);
                break;
            }
            results.add(tmp.substring(0, j));
            count++;
            tmp = tmp.substring(j + 1);
            if (count == limit || tmp.isEmpty()) {
                results.add(tmp);
                break;
            }
        }
        return results;
    }
}
