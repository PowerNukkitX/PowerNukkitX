package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.form.window.Form;

import java.util.Map;

/**
 * @author CreeperFace
 */
public class PlayerServerSettingsRequestEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Map<Integer, Form<?>> settings;

    public PlayerServerSettingsRequestEvent(Player player, Map<Integer, Form<?>> settings) {
        this.player = player;
        this.settings = settings;
    }

    public Map<Integer, Form<?>> getSettings() {
        return settings;
    }

    public void setSettings(Map<Integer, Form<?>> settings) {
        this.settings = settings;
    }

    public void setSettings(int id, Form<?> window) {
        this.settings.put(id, window);
    }
}
