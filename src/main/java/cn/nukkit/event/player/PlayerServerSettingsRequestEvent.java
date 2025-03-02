package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.form.window.Form;

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
