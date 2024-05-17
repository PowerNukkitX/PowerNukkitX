package cn.nukkit.level.format;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public abstract class LevelProviderManager {
    protected static final Map<String, Class<? extends LevelProvider>> providers = new HashMap<>();

    public static void addProvider(String name, Class<? extends LevelProvider> clazz) {
        if (providers.putIfAbsent(name.trim().toLowerCase(Locale.ENGLISH), clazz) != null) {
            log.error("Duplicate registration Level Provider {}", clazz);
        }
    }

    public static Class<? extends LevelProvider> getProvider(String path) {
        for (Class<? extends LevelProvider> provider : providers.values()) {
            try {
                if ((boolean) provider.getMethod("isValid", String.class).invoke(null, path)) {
                    return provider;
                }
            } catch (Exception e) {
                log.error("An error occurred while getting the provider {}", path, e);
            }
        }
        return null;
    }

    public static String getProviderName(Class<? extends LevelProvider> clazz) {
        for (var entry : providers.entrySet()) {
            if (clazz == entry.getValue()) {
                return entry.getKey();
            }
        }
        return "unknown";
    }

    public static Class<? extends LevelProvider> getProviderByName(String name) {
        name = name.trim().toLowerCase(Locale.ENGLISH);
        return providers.getOrDefault(name, null);
    }

}
