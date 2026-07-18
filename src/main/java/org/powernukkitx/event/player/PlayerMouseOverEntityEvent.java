package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.HandlerList;

public class PlayerMouseOverEntityEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity entity;

    public PlayerMouseOverEntityEvent(Player player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
