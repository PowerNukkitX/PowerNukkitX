package org.powernukkitx.level.format;

import org.powernukkitx.level.Level;

/**
 * Creates {@link LevelProvider} instances for a world format.
 * <p>
 * Plugins can implement this interface and register it through
 * {@link LevelProviderManager#registerProvider(LevelProviderFactory)} to add their own world format.
 */
public interface LevelProviderFactory {
    /**
     * @return the name of the format, as it is written in the {@code format} field of the level config.json
     */
    String getName();

    /**
     * @return the provider type created by this factory
     */
    Class<? extends LevelProvider> getProviderClass();

    /**
     * Opens the world stored at the given path.
     *
     * @param level the level the provider belongs to
     * @param path  the world folder
     * @return the opened provider
     */
    LevelProvider create(Level level, String path) throws Exception;

    /**
     * @param path the world folder
     * @return whether the world at the given path is stored in this format
     */
    boolean isValid(String path);

    /**
     * Writes the files a world of this format needs before it can be opened.
     *
     * @param path            the world folder
     * @param name            the world name
     * @param generatorConfig the config of the dimension being created
     */
    void generate(String path, String name, LevelConfig.GeneratorConfig generatorConfig) throws Exception;
}
