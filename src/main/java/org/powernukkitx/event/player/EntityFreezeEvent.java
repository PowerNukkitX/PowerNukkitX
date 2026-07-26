package org.powernukkitx.event.player;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.event.entity.EntityEvent;

public class EntityFreezeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityFreezeEvent(Entity human) {
        this.entity = human;
    }
}
