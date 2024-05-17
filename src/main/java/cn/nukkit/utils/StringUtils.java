package cn.nukkit.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;


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

    @NotNull public static String capitalize(@NotNull String str) {
        if (str.length() == 0) {
            return "";
        }
        if (str.length() == 1) {
            return str.toUpperCase(Locale.ENGLISH);
        }
        return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1);
    }

    /**
     * @see #fastSplit(String, String, int)
     */

    public static List<String> fastSplit(String delimiter, String str) {
        return fastSplit(delimiter, str, Integer.MAX_VALUE);
    }

    /**
     * 在短字符串上(通常只有一个分割)处理比{@link String#split(String)}快
     * <p>
     * Processing on short strings(There is usually only one split) is faster than {@link String#split(String)}
     *
     * @param delimiter the delimiter
     * @param str       the str
     * @param limit     the limit
     * @return the list
     */

    public static List<String> fastSplit(String delimiter, String str, int limit) {
        var tmp = str;
        var results = new ArrayList<String>();
        var count = 1;
        while (true) {
            int j = tmp.indexOf(delimiter);
            if (j < 0) {
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

    public static String joinNotNull(String delim, String... elements) {
        StringJoiner join = new StringJoiner(delim);
        for (var element : elements) {
            if (element != null) {
                join.add(element);
            }
        }
        return join.toString();
    }
}
