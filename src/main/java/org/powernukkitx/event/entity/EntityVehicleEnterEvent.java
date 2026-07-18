package org.powernukkitx.event.entity;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

public class EntityVehicleEnterEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity vehicle;

    public EntityVehicleEnterEvent(Entity entity, Entity vehicle) {
        this.entity = entity;
        this.vehicle = vehicle;
    }

    public Entity getVehicle() {
        return vehicle;
    }

}
