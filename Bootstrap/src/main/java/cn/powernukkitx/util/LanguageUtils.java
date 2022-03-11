package cn.powernukkitx.util;

import cn.powernukkitx.Bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LanguageUtils {
    public static final Locale locale = Locale.getDefault();
    private static Map<String, String> langMap;

    public static void init() {
        try (final InputStream stream = Bootstrap.class.getClassLoader()
                .getResourceAsStream("lang/" + locale.toLanguageTag().toLowerCase() + "/lang.ini")) {
            if (stream != null) {
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                    langMap = parseLang(reader);
                }
            }
        } catch (IOException e) {
            langMap = new HashMap<>(0);
        }
    }

    /**
     * 本地化键名
     *
     * @param key 键名
     */
    public static String tr(String key) {
        return langMap.getOrDefault(key, key);
    }

    private static Map<String, String> parseLang(BufferedReader reader) throws IOException {
        Map<String, String> d = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.charAt(0) == '#') {
                continue;
            }
            String[] t = line.split("=", 2);
            if (t.length < 2) {
                continue;
            }
            String key = t[0];
            String value = t[1];
            if (value.length() > 1 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
                value = value.substring(1, value.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
            }
            if (value.isEmpty()) {
                continue;
            }
            d.put(key, value);
        }
        return d;
    }
}
