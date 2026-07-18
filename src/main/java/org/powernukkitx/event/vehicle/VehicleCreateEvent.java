package org.powernukkitx.event.vehicle;


import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;


public class VehicleCreateEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public VehicleCreateEvent(Entity vehicle) {
        super(vehicle);
    }

}
