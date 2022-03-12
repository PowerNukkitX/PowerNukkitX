package cn.powernukkitx.bootstrap.util;

import cn.powernukkitx.bootstrap.Bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class ConfigUtils {
    private static Map<String, String> configMap;

    public static void init() {
        try (final InputStream stream = Bootstrap.class.getClassLoader()
                .getResourceAsStream("config.ini")) {
            if (stream != null) {
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    configMap = INIUtils.parseINI(reader);
                }
            }
        } catch (IOException e) {
            configMap = new HashMap<>(0);
        }
    }

    public static String graalvmVersion() {
        return configMap.get("graalvm.version");
    }

    public static String adoptOpenJDKVersion() {
        return configMap.get("adopt.version");
    }

    public static String get(String key) {
        return configMap.get(key);
    }
}
