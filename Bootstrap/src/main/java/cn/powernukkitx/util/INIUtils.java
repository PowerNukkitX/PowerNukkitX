package cn.powernukkitx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class INIUtils {
    public static Map<String, String> parseINI(BufferedReader reader) throws IOException {
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
                value = value.substring(1, value.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\").replace("\\n", "\n");
            }
            if (value.isEmpty()) {
                continue;
            }
            d.put(key, value);
        }
        return d;
    }
}
