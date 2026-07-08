package org.powernukkitx.plugin;

/**
 * 描述一个Nukkit插件加载顺序的类。<br>
 * Describes a Nukkit plugin load order.
 * <p>
 * <p>Nukkit插件的加载顺序有两个:{@link PluginLoadOrder#STARTUP}
 * 和 {@link PluginLoadOrder#POSTWORLD}。<br>
 * The load order of a Nukkit plugin can be {@link PluginLoadOrder#STARTUP}
 * or {@link PluginLoadOrder#POSTWORLD}.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author iNevet(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public enum PluginLoadOrder {
    /**
     * 表示这个插件在服务器启动时就开始加载。<br>
     * Indicates that the plugin will be loaded at startup.
     *
     * @see PluginLoadOrder
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    STARTUP,
    /**
     * 表示这个插件在第一个世界加载完成后开始加载。<br>
     * Indicates that the plugin will be loaded after the first/default world was created.
     *
     * @see PluginLoadOrder
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    POSTWORLD
}
