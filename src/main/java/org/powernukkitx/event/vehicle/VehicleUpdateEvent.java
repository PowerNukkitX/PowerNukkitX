package org.powernukkitx.event.vehicle;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.HandlerList;


public class VehicleUpdateEvent extends VehicleEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public VehicleUpdateEvent(Entity vehicle) {
        super(vehicle);
    }

}
