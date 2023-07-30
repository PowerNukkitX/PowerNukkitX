package cn.nukkit.event.vehicle;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;

/**
 * Is called when an vehicle gets destroyed
 */
public class VehicleDestroyEvent extends VehicleEvent implements Cancellable {

    /**
     * Constructor for the VehicleDestroyEvent
     *
     * @param vehicle the destroyed vehicle
     */
    @PowerNukkitOnly
    public VehicleDestroyEvent(final Entity vehicle) {
        super(vehicle);
    }
}
