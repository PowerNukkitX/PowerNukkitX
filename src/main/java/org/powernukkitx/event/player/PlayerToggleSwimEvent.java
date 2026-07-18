package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

/**
 * @author CreeperFace
 */
public class PlayerToggleSwimEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final boolean isSwimming;

    public PlayerToggleSwimEvent(Player player, boolean isSwimming) {
        this.player = player;
        this.isSwimming = isSwimming;
    }

    public boolean isSwimming() {
        return this.isSwimming;
    }
}
