package org.powernukkitx.event.entity;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

public class EntityPortalEnterEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final PortalType type;

    public EntityPortalEnterEvent(Entity entity, PortalType type) {
        this.entity = entity;
        this.type = type;
    }

    public PortalType getPortalType() {
        return type;
    }

    public enum PortalType {
        NETHER,
        END
    }
}
