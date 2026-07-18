package org.powernukkitx.plugin;

import org.powernukkitx.Server;
import org.powernukkitx.command.CommandExecutor;
import org.powernukkitx.utils.Config;

import java.io.File;
import java.io.InputStream;

/**
 * Represents the base contract that all Nukkit plugins must implement.
 *
 * <p>Plugin developers should usually extend {@link PluginBase} instead of
 * implementing this interface directly.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @see PluginBase
 * @see PluginDescription
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface Plugin extends CommandExecutor {


    Plugin[] EMPTY_ARRAY = new Plugin[0];

    /**
     * Called when this plugin is loaded, before {@link #onEnable()}.
     *
     * <p>Use this method to initialize internal data structures, load static resources,
     * or prepare database connections before the plugin is enabled.</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void onLoad();

    /**
     * Called when this plugin is enabled.
     *
     * <p>Use this method to open configuration files, register listeners,
     * connect to databases, or start plugin services.</p>
     *
     * <p>This method may be called more than once if the plugin is disabled and
     * enabled again by a plugin manager.</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void onEnable();

    /**
     * Returns whether this plugin is currently enabled.
     *
     * @return {@code true} if this plugin is enabled
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isEnabled();

    /**
     * Called when this plugin is disabled.
     *
     * <p>Use this method to stop tasks, save data, close database connections,
     * unregister resources, and release anything opened by the plugin.</p>
     *
     * <p>This method may be called more than once if the plugin is restarted
     * by a plugin manager.</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void onDisable();

    /**
     * Called synchronously before the server begins its shutdown process.
     *
     * <p>This hook allows plugins to perform early shutdown work before the normal
     * disable phase starts. Shutdown will wait until this method returns.</p>
     */
    default void beforeStop() {

    }

    /**
     * Returns whether this plugin is currently disabled.
     *
     * @return {@code true} if this plugin is disabled
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isDisabled();

    /**
     * Returns the data folder of this plugin.
     *
     * <p>Under normal circumstances, the data folder has the same name as the plugin
     * and is placed in the {@code plugins} folder inside the Nukkit installation directory.</p>
     *
     * @return the data folder of this plugin
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    File getDataFolder();

    /**
     * Returns the description of this plugin.
     *
     * <p>For jar-packed plugins, the description is defined in the {@code plugin.yml} file.</p>
     *
     * @return the description of this plugin
     * @see PluginDescription
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginDescription getDescription();

    /**
     * Opens a resource from this plugin and returns it as an {@link InputStream}.
     *
     * <p>For jar-packed plugins, Nukkit looks for the resource inside the plugin jar,
     * usually in the plugin resources directory.</p>
     *
     * <p>To read the whole resource as a {@link String}, use
     * {@link org.powernukkitx.utils.Utils#readFile(InputStream)}:</p>
     *
     * <pre>{@code
     * String string = Utils.readFile(this.getResource("string.txt"));
     * }</pre>
     *
     * @param filename the name of the resource file to read
     * @return the resource as an {@link InputStream}, or {@code null} if the resource could not be found or opened
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    InputStream getResource(String filename);

    /**
     * Saves a plugin resource to this plugin's data folder without replacing an existing file.
     *
     * <p>For jar-packed plugins, Nukkit looks for the resource inside the plugin jar
     * and copies it into the plugin data folder.</p>
     *
     * <p>This is usually used to save default plugin resources during the load phase,
     * so plugin users do not need to manually copy default resources into the data folder.</p>
     *
     * <p>To replace an existing resource file, use {@link #saveResource(String, boolean)}.</p>
     *
     * @param filename the name of the resource file to save
     * @return {@code true} if the resource was saved successfully
     * @see #saveDefaultConfig()
     * @see #saveResource(String, boolean)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean saveResource(String filename);

    /**
     * Saves a plugin resource to this plugin's data folder.
     *
     * <p>For jar-packed plugins, Nukkit looks for the resource inside the plugin jar
     * and copies it into the plugin data folder.</p>
     *
     * @param filename the name of the resource file to save
     * @param replace  whether to replace the target file if it already exists
     * @return {@code true} if the resource was saved successfully
     * @see #saveResource(String)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean saveResource(String filename, boolean replace);

    /**
     * Saves a plugin resource to this plugin's data folder using a custom output name.
     *
     * <p>For jar-packed plugins, Nukkit looks for the resource inside the plugin jar
     * and copies it into the plugin data folder. If {@code replace} is {@code false},
     * an existing target file will not be overwritten.</p>
     *
     * @param filename   the name of the resource inside the plugin jar
     * @param outputName the target file name or relative path in the plugin data folder
     * @param replace    whether to replace the target file if it already exists
     * @return {@code true} if the resource was saved successfully
     * @see #saveResource(String)
     * @see #saveResource(String, boolean)
     */
    boolean saveResource(String filename, String outputName, boolean replace);

    /**
     * Returns the configuration of this plugin.
     *
     * <p>Normally, the plugin configuration is saved in the {@code config.yml} file
     * in the plugin data folder.</p>
     *
     * @return the configuration of this plugin
     * @see #getDataFolder()
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Config getConfig();

    /**
     * Saves this plugin's current configuration to disk.
     *
     * @see #getDataFolder()
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void saveConfig();

    /**
     * Saves the default plugin configuration to the plugin data folder.
     *
     * <p>Nukkit looks for the default {@code config.yml} file provided by the plugin
     * developer and saves it to the plugin data folder. If {@code config.yml} already
     * exists in the data folder, it will not be replaced.</p>
     *
     * <p>This is usually used during the load phase to ensure the plugin has a default
     * configuration file before it is enabled.</p>
     *
     * @see #getDataFolder()
     * @see #saveResource(String)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void saveDefaultConfig();

    /**
     * Reloads this plugin's configuration from disk.
     *
     * <p>This reloads the configuration from {@code config.yml}, allowing changes
     * to be applied without restarting the server.</p>
     *
     * @see #getDataFolder()
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void reloadConfig();

    /**
     * Returns the server instance that is running this plugin.
     *
     * @return the server instance
     * @see Server
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Server getServer();

    /**
     * Returns the name of this plugin.
     *
     * <p>The plugin name is read from the plugin description.</p>
     *
     * @return the plugin name
     * @see #getDescription()
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    String getName();

    /**
     * Returns this plugin's logger.
     *
     * <p>The logger can be used to output messages to the console and log file.</p>
     *
     * @return this plugin's logger
     * @see PluginLogger
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginLogger getLogger();

    /**
     * Returns the loader responsible for loading this plugin.
     *
     * @return this plugin's loader
     * @see PluginLoader
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginLoader getPluginLoader();

    /**
     * Returns the class loader used to load this plugin.
     *
     * @return this plugin's class loader
     */
    ClassLoader getPluginClassLoader();

    /**
     * Returns the file that contains this plugin.
     *
     * @return the plugin file
     */
    File getFile();
}
