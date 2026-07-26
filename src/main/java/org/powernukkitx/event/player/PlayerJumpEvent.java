package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.HandlerList;

public class PlayerJumpEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerJumpEvent(Player player){
        this.player = player;
    }
}
