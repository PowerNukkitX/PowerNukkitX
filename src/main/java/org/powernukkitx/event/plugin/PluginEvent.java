package org.powernukkitx.event.plugin;

import org.powernukkitx.event.Event;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.plugin.Plugin;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PluginEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Plugin plugin;

    public PluginEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
