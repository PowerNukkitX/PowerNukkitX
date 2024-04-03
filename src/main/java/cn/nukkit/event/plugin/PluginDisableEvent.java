package cn.nukkit.event.plugin;

import cn.nukkit.event.HandlerList;
import cn.nukkit.plugin.Plugin;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PluginDisableEvent extends PluginEvent {
    public PluginDisableEvent(Plugin plugin) {
        super(plugin);
    }
}
