package cn.nukkit.event.plugin;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.plugin.Plugin;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PluginEvent extends Event {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final Plugin plugin;

    public PluginEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
