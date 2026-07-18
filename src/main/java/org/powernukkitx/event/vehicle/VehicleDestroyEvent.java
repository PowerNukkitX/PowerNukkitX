package org.powernukkitx.event.vehicle;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;


/**
 * Is called when an vehicle gets destroyed
 */
public class VehicleDestroyEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Constructor for the VehicleDestroyEvent
     *
     * @param vehicle the destroyed vehicle
     */
    public VehicleDestroyEvent(final Entity vehicle) {
        super(vehicle);
    }

}
