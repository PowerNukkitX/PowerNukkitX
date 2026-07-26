package org.powernukkitx.event.vehicle;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;


public class EntityExitVehicleEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final org.powernukkitx.entity.Entity riding;

    public EntityExitVehicleEvent(org.powernukkitx.entity.Entity riding, Entity vehicle) {
        super(vehicle);
        this.riding = riding;
    }

    public org.powernukkitx.entity.Entity getEntity() {
        return riding;
    }

    public boolean isPlayer() {
        return riding instanceof Player;
    }

}
