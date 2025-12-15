package cn.nukkit.config.legacy;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.io.File;

public class LegacyServerProperties {
    private final Config properties;

    public LegacyServerProperties(String dataPath) {
        File file = new File(dataPath + "server.properties");

        // Create file if it doesn't exist
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Load existing properties
        this.properties = new Config(dataPath + "server.properties", Config.PROPERTIES);

        // Check and add missing properties with defaults (preserves existing values)
        boolean modified = false;

        // Basic Server Settings
        if (!this.properties.exists("server-ip")) {
            this.properties.set("server-ip", "0.0.0.0");
            modified = true;
        }
        if (!this.properties.exists("server-port")) {
            this.properties.set("server-port", 19132);
            modified = true;
        }
        if (!this.properties.exists("max-players")) {
            this.properties.set("max-players", 20);
            modified = true;
        }
        if (!this.properties.exists("motd")) {
            this.properties.set("motd", "PowerNukkitX Server");
            modified = true;
        }
        if (!this.properties.exists("sub-motd")) {
            this.properties.set("sub-motd", "powernukkitx.org");
            modified = true;
        }
        if (!this.properties.exists("level-name")) {
            this.properties.set("level-name", "world");
            modified = true;
        }
        if (!this.properties.exists("language")) {
            this.properties.set("language", "eng");
            modified = true;
        }

        // Authentication & Security
        if (!this.properties.exists("xbox-auth")) {
            this.properties.set("xbox-auth", true);
            modified = true;
        }
        if (!this.properties.exists("white-list")) {
            this.properties.set("white-list", false);
            modified = true;
        }

        // Gameplay Settings
        if (!this.properties.exists("achievements")) {
            this.properties.set("achievements", true);
            modified = true;
        }
        if (!this.properties.exists("announce-player-achievements")) {
            this.properties.set("announce-player-achievements", true);
            modified = true;
        }
        if (!this.properties.exists("gamemode")) {
            this.properties.set("gamemode", 0);
            modified = true;
        }
        if (!this.properties.exists("force-gamemode")) {
            this.properties.set("force-gamemode", false);
            modified = true;
        }
        if (!this.properties.exists("hardcore")) {
            this.properties.set("hardcore", false);
            modified = true;
        }
        if (!this.properties.exists("difficulty")) {
            this.properties.set("difficulty", 1);
            modified = true;
        }
        if (!this.properties.exists("pvp")) {
            this.properties.set("pvp", true);
            modified = true;
        }

        // World & Spawn Settings
        if (!this.properties.exists("spawn-protection")) {
            this.properties.set("spawn-protection", 16);
            modified = true;
        }
        if (!this.properties.exists("spawn-radius")) {
            this.properties.set("spawn-radius", 16);
            modified = true;
        }
        if (!this.properties.exists("view-distance")) {
            this.properties.set("view-distance", 8);
            modified = true;
        }
        if (!this.properties.exists("allow-nether")) {
            this.properties.set("allow-nether", true);
            modified = true;
        }
        if (!this.properties.exists("allow-the-end")) {
            this.properties.set("allow-the-end", true);
            modified = true;
        }

        // Player Settings
        if (!this.properties.exists("allow-flight")) {
            this.properties.set("allow-flight", false);
            modified = true;
        }
        if (!this.properties.exists("enable-command-block")) {
            this.properties.set("enable-command-block", true);
            modified = true;
        }

        // Server Management
        if (!this.properties.exists("auto-save")) {
            this.properties.set("auto-save", true);
            modified = true;
        }
        if (!this.properties.exists("force-resources")) {
            this.properties.set("force-resources", false);
            modified = true;
        }
        if (!this.properties.exists("allow-client-packs")) {
            this.properties.set("allow-client-packs", true);
            modified = true;
        }
        if (!this.properties.exists("safe-spawn")) {
            this.properties.set("safe-spawn", true);
            modified = true;
        }

        // Network & Movement
        if (!this.properties.exists("server-authoritative-movement")) {
            this.properties.set("server-authoritative-movement", "server-auth");
            modified = true;
        }
        if (!this.properties.exists("enable-query")) {
            this.properties.set("enable-query", true);
            modified = true;
        }
        if (!this.properties.exists("allow-beta")) {
            this.properties.set("allow-beta", false);
            modified = true;
        }

        // Misc
        if (!this.properties.exists("shutdown-message")) {
            this.properties.set("shutdown-message", "Server closed");
            modified = true;
        }

        // Save only if we added missing properties
        if (modified) {
            this.properties.save();
        }
    }

    public void save() {
        this.properties.save();
    }

    public void reload() {
        this.properties.reload();
    }

    public ConfigSection getProperties() {
        return this.properties.getRootSection();
    }

    public Integer get(LegacyServerPropertiesKeys key, Integer defaultValue) {
        Object value = this.properties.get(key.toString());
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                // Log the error or handle it as needed
                return defaultValue;
            }
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return defaultValue;
        }
    }

    public String get(LegacyServerPropertiesKeys key, String defaultValue) {
        Object value = this.properties.get(key.toString());
        if (value instanceof String) {
            return (String) value;
        } else {
            return defaultValue;
        }
    }

    public Boolean get(LegacyServerPropertiesKeys key, Boolean defaultValue) {
        Object value = this.properties.get(key.toString());
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return defaultValue;
        }
    }

    public Long get(LegacyServerPropertiesKeys key, Long defaultValue) {
        Object value = this.properties.get(key.toString());
        if (value instanceof String stringValue) {
            if (!stringValue.isEmpty()) {
                try {
                    return Long.parseLong(stringValue);
                } catch (NumberFormatException e) {
                    // Log the error or handle it as needed
                    return defaultValue;
                }
            } else {
                return defaultValue;
            }
        } else if (value instanceof Long) {
            return (Long) value;
        } else {
            return defaultValue;
        }
    }

    public void set(String key, Object value) {
        this.properties.set(key, value);
    }

    public void remove(String key) {
        this.properties.remove(key);
    }

    public boolean exists(String key) {
        return this.properties.exists(key);
    }
}
