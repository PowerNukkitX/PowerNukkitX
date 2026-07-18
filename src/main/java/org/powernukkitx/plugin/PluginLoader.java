package org.powernukkitx.plugin;

import java.io.File;
import java.util.regex.Pattern;

/**
 * An interface to describe a plugin loader.
 *
 * @author iNevet(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @see JavaPluginLoader
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface PluginLoader {

    /**
     * Loads and initializes a plugin by its file name.
     *
     * <p>Properties for loaded plugin should be set in this method. Such as, the {@code Server} object for which this
     * plugin is running in, the {@code PluginLoader} object for its loader, and the {@code File} object for its
     * data folder.</p>
     *
     * <p>If the plugin loader does not load this plugin successfully, a {@code null} should be returned,
     * or an exception should be thrown.</p>
     *
     * @param filename A string of its file name.
     * @return The loaded plugin as a {@code Plugin} object.
     * @throws java.lang.Exception Thrown when an error occurred.
     * @see #loadPlugin(File)
     * @see org.powernukkitx.plugin.PluginBase#init(PluginLoader, ClassLoader, org.powernukkitx.Server, PluginDescription, File, File)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Plugin loadPlugin(String filename) throws Exception;

    /**
     * Loads and initializes a plugin by a {@code File} object describes the file.
     *
     * <p>Properties for loaded plugin should be set in this method. Such as, the {@code Server} object for which this
     * plugin is running in, the {@code PluginLoader} object for its loader, and the {@code File} object for its
     * data folder.</p>
     *
     * <p>If the plugin loader does not load this plugin successfully, a {@code null} should be returned,
     * or an exception should be thrown.</p>
     *
     * @param file A {@code File} object for this plugin.
     * @return The loaded plugin as a {@code Plugin} object.
     * @throws java.lang.Exception Thrown when an error occurred.
     * @see #loadPlugin(String)
     * @see org.powernukkitx.plugin.PluginBase#init(PluginLoader, ClassLoader, org.powernukkitx.Server, PluginDescription, File, File)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Plugin loadPlugin(File file) throws Exception;

    /**
     * Gets a {@code PluginDescription} object describes the plugin by its file name.
     *
     * <p>If the plugin loader does not get its description successfully, a {@code null} should be returned.</p>
     *
     * @param filename A string of its file name.
     * @return A {@code PluginDescription} object describes the plugin.
     * @see #getPluginDescription(File)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginDescription getPluginDescription(String filename);

    /**
     * Gets a {@code PluginDescription} object describes the plugin by a {@code File} object describes the plugin file.
     *
     * <p>If the plugin loader does not get its description successfully, a {@code null} should be returned.</p>
     *
     * @param file A {@code File} object for this plugin.
     * @return A {@code PluginDescription} object describes the plugin.
     * @see #getPluginDescription(String)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginDescription getPluginDescription(File file);

    /**
     * Returns the file types this plugin loader supports.
     *
     * <p>When Nukkit is trying to load all its plugins, the plugin manager will look for all installed plugin loader,
     * and choose the correct one by checking if this plugin matches the filters given below.</p>
     *
     * <p>For example, to check if this file is has a "jar" extension, the regular expression should be:<br>
     * {@code ^.+\\.jar$}<br>
     * So, for a jar-extension-only file plugin loader, this method should be:
     * </p>
     * <pre> {@code           @Override}
     *      public Pattern[] getPluginFilters() {
     *          return new Pattern[]{Pattern.compile("^.+\\.jar$")};
     *      }
     * </pre>
     *
     * @return An array of regular expressions, that describes what kind of file this plugin loader supports.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Pattern[] getPluginFilters();

    /**
     * Enables a plugin.
     *
     * @param plugin The plugin to enable.
     * @see #disablePlugin
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void enablePlugin(Plugin plugin);

    /**
     * Disables a plugin.
     *
     * @param plugin The plugin to disable.
     * @see #enablePlugin
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void disablePlugin(Plugin plugin);

}
