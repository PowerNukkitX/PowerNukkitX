package cn.nukkit.config.legacy;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.io.File;

public class LegacyServerProperties {
    private final Config properties;

    public LegacyServerProperties(String dataPath) {
        File file = new File(dataPath + "server.properties");
        if (!file.exists()) {
            new Config(file.getPath(), Config.PROPERTIES).save();
        }
        this.properties = new Config(dataPath + "server.properties", Config.PROPERTIES);
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

