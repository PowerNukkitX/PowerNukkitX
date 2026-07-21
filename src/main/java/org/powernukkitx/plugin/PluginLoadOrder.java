package org.powernukkitx.plugin;

/**
 * Describes a Nukkit plugin load order.
 * <p>
 * <p>The load order of a Nukkit plugin can be {@link org.powernukkitx.plugin.PluginLoadOrder#STARTUP}
 * or {@link org.powernukkitx.plugin.PluginLoadOrder#POSTWORLD}.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author iNevet(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public enum PluginLoadOrder {
    /**
     * Indicates that the plugin will be loaded at startup.
     *
     * @see org.powernukkitx.plugin.PluginLoadOrder
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    STARTUP,
    /**
     * Indicates that the plugin will be loaded after the first/default world was created.
     *
     * @see org.powernukkitx.plugin.PluginLoadOrder
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    POSTWORLD
}
