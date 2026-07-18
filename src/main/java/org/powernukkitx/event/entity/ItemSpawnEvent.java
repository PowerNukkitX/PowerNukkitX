package org.powernukkitx.event.entity;

import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class ItemSpawnEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ItemSpawnEvent(EntityItem item) {
        this.entity = item;
    }

    @Override
    public EntityItem getEntity() {
        return (EntityItem) this.entity;
    }
}
