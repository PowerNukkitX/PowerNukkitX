package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.entity.data.human.Skin;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

/**
 * @author KCodeYT (Nukkit Project)
 */
public class PlayerChangeSkinEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Skin skin;

    public PlayerChangeSkinEvent(Player player, Skin skin) {
        this.player = player;
        this.skin = skin;
    }

    public Skin getSkin() {
        return this.skin;
    }

}
