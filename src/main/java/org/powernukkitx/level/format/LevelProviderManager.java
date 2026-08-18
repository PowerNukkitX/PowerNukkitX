package org.powernukkitx.level.format;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public abstract class LevelProviderManager {
    protected static final Map<String, LevelProviderFactory> providers = new LinkedHashMap<>();

    /**
     * Registers a world format. The name is the value plugins and worlds use in the {@code format} field of
     * their config.json.
     *
     * @param factory the factory creating the providers of the format
     */
    public static void registerProvider(LevelProviderFactory factory) {
        if (providers.putIfAbsent(factory.getName().trim().toLowerCase(Locale.ENGLISH), factory) != null) {
            log.error("Duplicate registration Level Provider {}", factory.getName());
        }
    }

    /**
     * Registers a world format from a provider class following the legacy contract.
     *
     * @see ReflectiveLevelProviderFactory
     */
    public static void addProvider(String name, Class<? extends LevelProvider> clazz) {
        registerProvider(new ReflectiveLevelProviderFactory(name, clazz));
    }

    /**
     * @param path the world folder
     * @return the factory able to open the world, or null if no registered format matches it
     */
    public static @Nullable LevelProviderFactory getProviderFactory(String path) {
        for (LevelProviderFactory factory : providers.values()) {
            try {
                if (factory.isValid(path)) {
                    return factory;
                }
            } catch (Exception e) {
                log.error("An error occurred while getting the provider {}", path, e);
            }
        }
        return null;
    }

    public static @Nullable LevelProviderFactory getProviderFactoryByName(String name) {
        return providers.get(name.trim().toLowerCase(Locale.ENGLISH));
    }

    public static Map<String, LevelProviderFactory> getProviderFactories() {
        return Collections.unmodifiableMap(providers);
    }

    public static @Nullable Class<? extends LevelProvider> getProvider(String path) {
        LevelProviderFactory factory = getProviderFactory(path);
        return factory == null ? null : factory.getProviderClass();
    }

    public static String getProviderName(Class<? extends LevelProvider> clazz) {
        for (var entry : providers.entrySet()) {
            if (clazz == entry.getValue().getProviderClass()) {
                return entry.getKey();
            }
        }
        return "unknown";
    }

    public static @Nullable Class<? extends LevelProvider> getProviderByName(String name) {
        LevelProviderFactory factory = getProviderFactoryByName(name);
        return factory == null ? null : factory.getProviderClass();
    }

}
