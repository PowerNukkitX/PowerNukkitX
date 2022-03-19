package cn.powernukkitx.bootstrap.util;

import cn.powernukkitx.bootstrap.Bootstrap;

import java.io.*;
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
        final File configFile = new File("bootstrap.ini");
        if (configFile.exists() && configFile.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                configMap.putAll(INIUtils.parseINI(reader));
            } catch (IOException ignore) {

            }
        }
    }

    public static String graalvmVersion() {
        return configMap.get("graalvm.version");
    }

    public static String adoptOpenJDKVersion() {
        return configMap.get("adopt.version");
    }

    public static String startCommand() {
        return configMap.get("start-cmd");
    }

    public static int minRestartTime() {
        return Integer.parseInt(configMap.getOrDefault("min-restart-time", "30000"));
    }

    public static boolean autoRestart() {
        return Boolean.parseBoolean(configMap.getOrDefault("auto-restart", "false"));
    }

    public static String get(String key) {
        return configMap.get(key);
    }

    public static String forceLang() {
        return configMap.get("language");
    }
}
