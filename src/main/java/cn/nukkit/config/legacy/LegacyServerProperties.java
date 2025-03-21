package cn.nukkit.config.legacy;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.io.File;

public class LegacyServerProperties {
    private final Config properties;

    public LegacyServerProperties(String dataPath) {
        File file = new File(dataPath + "server.properties");
        if (!file.exists()) {
            ConfigSection defaults = getDefaultValues();
            new Config(file.getPath(), Config.PROPERTIES, defaults).save();
        }
        this.properties = new Config(dataPath + "server.properties", Config.PROPERTIES, getDefaultValues());
    }

    private ConfigSection getDefaultValues() {
        ConfigSection defaults = new ConfigSection();
        defaults.put(LegacyServerPropertiesKeys.MOTD.toString(), "PowerNukkitX Server");
        defaults.put(LegacyServerPropertiesKeys.SUB_MOTD.toString(), "powernukkitx.org");
        defaults.put(LegacyServerPropertiesKeys.SERVER_IP.toString(), "0.0.0.0");
        defaults.put(LegacyServerPropertiesKeys.SERVER_PORT.toString(), 19132);
        defaults.put(LegacyServerPropertiesKeys.VIEW_DISTANCE.toString(), 8);
        defaults.put(LegacyServerPropertiesKeys.WHITE_LIST.toString(), false);
        defaults.put(LegacyServerPropertiesKeys.ACHIEVEMENTS.toString(), true);
        defaults.put(LegacyServerPropertiesKeys.ANNOUNCE_PLAYER_ACHIEVEMENTS.toString(), true);
        defaults.put(LegacyServerPropertiesKeys.SPAWN_PROTECTION.toString(), 16);
        defaults.put(LegacyServerPropertiesKeys.MAX_PLAYERS.toString(), 20);
        defaults.put(LegacyServerPropertiesKeys.ALLOW_FLIGHT.toString(), false);
        defaults.put(LegacyServerPropertiesKeys.SPAWN_ANIMALS.toString(), true);
        defaults.put(LegacyServerPropertiesKeys.SPAWN_MOBS.toString(), true);
        defaults.put(LegacyServerPropertiesKeys.GAMEMODE.toString(), 0);
        defaults.put(LegacyServerPropertiesKeys.FORCE_GAMEMODE.toString(), false);
        defaults.put(LegacyServerPropertiesKeys.HARDCORE.toString(), false);
        defaults.put(LegacyServerPropertiesKeys.PVP.toString(), true);
        defaults.put(LegacyServerPropertiesKeys.DIFFICULTY.toString(), 1);
        defaults.put(LegacyServerPropertiesKeys.LEVEL_NAME.toString(), "world");
        defaults.put(LegacyServerPropertiesKeys.LEVEL_SEED.toString(), "");
        defaults.put(LegacyServerPropertiesKeys.ALLOW_NETHER.toString(), true);
        defaults.put(LegacyServerPropertiesKeys.ALLOW_THE_END.toString(), true);
        defaults.put(LegacyServerPropertiesKeys.USE_TERRA.toString(), false);
        defaults.put(LegacyServerPropertiesKeys.ENABLE_QUERY.toString(), false);
        defaults.put(LegacyServerPropertiesKeys.ENABLE_RCON.toString(), false);
        defaults.put(LegacyServerPropertiesKeys.RCON_PASSWORD.toString(), "");
        defaults.put(LegacyServerPropertiesKeys.AUTO_SAVE.toString(), true);
        defaults.put(LegacyServerPropertiesKeys.FORCE_RESOURCES.toString(), false);
        defaults.put(LegacyServerPropertiesKeys.FORCE_RESOURCES_ALLOW_CLIENT_PACKS.toString(), false);
        defaults.put(LegacyServerPropertiesKeys.XBOX_AUTH.toString(), true);
        defaults.put(LegacyServerPropertiesKeys.CHECK_LOGIN_TIME.toString(), false);
        defaults.put(LegacyServerPropertiesKeys.SERVER_AUTHORITATIVE_MOVEMENT.toString(), "server-auth");
        defaults.put(LegacyServerPropertiesKeys.NETWORK_ENCRYPTION.toString(), true);
        return defaults;
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

