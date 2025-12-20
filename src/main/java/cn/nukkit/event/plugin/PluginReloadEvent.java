package cn.nukkit.event.plugin;

import cn.nukkit.event.Cancellable;
import cn.nukkit.plugin.Plugin;

/**
 * Triggers before plugin is reloaded.
 *
 * @author xRookieFight
 * @since 20/12/2025
 */
public class PluginReloadEvent extends PluginEvent implements Cancellable {
    public PluginReloadEvent(Plugin plugin) {
        super(plugin);
    }
}
