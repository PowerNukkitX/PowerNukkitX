package org.powernukkitx.event.vehicle;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Event;

/**
 * @author larryTheCoder (Nukkit Project) 
 * @since 7/5/2017
 */
public abstract class VehicleEvent extends Event {

    private final Entity vehicle;

    public VehicleEvent(Entity vehicle) {
        this.vehicle = vehicle;
    }

    public Entity getVehicle() {
        return vehicle;
    }
}
