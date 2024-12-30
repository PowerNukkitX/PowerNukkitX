package cn.nukkit.config;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.io.File;

public class ServerProperties {
    private final Config properties;

    public ServerProperties(String dataPath) {
        File file = new File(dataPath + "server.properties");
        if (!file.exists()) {
            ConfigSection defaults = getDefaultValues();
            new Config(file.getPath(), Config.PROPERTIES, defaults).save();
        }
        this.properties = new Config(dataPath + "server.properties", Config.PROPERTIES, getDefaultValues());
    }

    private ConfigSection getDefaultValues() {
        ConfigSection defaults = new ConfigSection();
        defaults.put(ServerPropertiesKeys.MOTD.toString(), "PowerNukkitX Server");
        defaults.put(ServerPropertiesKeys.SUB_MOTD.toString(), "v2.powernukkitx.com");
        defaults.put(ServerPropertiesKeys.SERVER_IP.toString(), "0.0.0.0");
        defaults.put(ServerPropertiesKeys.SERVER_PORT.toString(), 19132);
        defaults.put(ServerPropertiesKeys.VIEW_DISTANCE.toString(), 8);
        defaults.put(ServerPropertiesKeys.WHITE_LIST.toString(), false);
        defaults.put(ServerPropertiesKeys.ACHIEVEMENTS.toString(), true);
        defaults.put(ServerPropertiesKeys.ANNOUNCE_PLAYER_ACHIEVEMENTS.toString(), true);
        defaults.put(ServerPropertiesKeys.SPAWN_PROTECTION.toString(), 16);
        defaults.put(ServerPropertiesKeys.MAX_PLAYERS.toString(), 20);
        defaults.put(ServerPropertiesKeys.ALLOW_FLIGHT.toString(), false);
        defaults.put(ServerPropertiesKeys.SPAWN_ANIMALS.toString(), true);
        defaults.put(ServerPropertiesKeys.SPAWN_MOBS.toString(), true);
        defaults.put(ServerPropertiesKeys.GAMEMODE.toString(), 0);
        defaults.put(ServerPropertiesKeys.FORCE_GAMEMODE.toString(), false);
        defaults.put(ServerPropertiesKeys.HARDCORE.toString(), false);
        defaults.put(ServerPropertiesKeys.PVP.toString(), true);
        defaults.put(ServerPropertiesKeys.DIFFICULTY.toString(), 1);
        defaults.put(ServerPropertiesKeys.LEVEL_NAME.toString(), "world");
        defaults.put(ServerPropertiesKeys.LEVEL_SEED.toString(), "");
        defaults.put(ServerPropertiesKeys.ALLOW_NETHER.toString(), true);
        defaults.put(ServerPropertiesKeys.ALLOW_THE_END.toString(), true);
        defaults.put(ServerPropertiesKeys.USE_TERRA.toString(), false);
        defaults.put(ServerPropertiesKeys.ENABLE_QUERY.toString(), false);
        defaults.put(ServerPropertiesKeys.ENABLE_RCON.toString(), false);
        defaults.put(ServerPropertiesKeys.RCON_PASSWORD.toString(), "");
        defaults.put(ServerPropertiesKeys.AUTO_SAVE.toString(), true);
        defaults.put(ServerPropertiesKeys.FORCE_RESOURCES.toString(), false);
        defaults.put(ServerPropertiesKeys.FORCE_RESOURCES_ALLOW_CLIENT_PACKS.toString(), false);
        defaults.put(ServerPropertiesKeys.XBOX_AUTH.toString(), true);
        defaults.put(ServerPropertiesKeys.CHECK_LOGIN_TIME.toString(), false);
        defaults.put(ServerPropertiesKeys.DISABLE_AUTO_BUG_REPORT.toString(), false);
        defaults.put(ServerPropertiesKeys.ALLOW_SHADED.toString(), true);
        defaults.put(ServerPropertiesKeys.SERVER_AUTHORITATIVE_MOVEMENT.toString(), "server-auth");
        defaults.put(ServerPropertiesKeys.NETWORK_ENCRYPTION.toString(), true);
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

    public Integer get(ServerPropertiesKeys key, Integer defaultValue) {
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

    public String get(ServerPropertiesKeys key, String defaultValue) {
        Object value = this.properties.get(key.toString());
        if (value instanceof String) {
            return (String) value;
        } else {
            return defaultValue;
        }
    }

    public Boolean get(ServerPropertiesKeys key, Boolean defaultValue) {
        Object value = this.properties.get(key.toString());
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return defaultValue;
        }
    }

    public Long get(ServerPropertiesKeys key, Long defaultValue) {
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

