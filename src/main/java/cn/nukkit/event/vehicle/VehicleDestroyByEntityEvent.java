package cn.nukkit.event.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.HandlerList;

/**
 * Is called when an entity destroyed a vehicle
 *
 * @author TrainmasterHD
 * @since 09.09.2019
 */

public final class VehicleDestroyByEntityEvent extends VehicleDestroyEvent {
    private final Entity destroyer;

    /**
     * Constructor for the VehicleDestroyByEntityEvent
     *
     * @param vehicle   the destroyed vehicle
     * @param destroyer the destroying entity
     */

    public VehicleDestroyByEntityEvent(final Entity vehicle, final Entity destroyer) {
        super(vehicle);

        this.destroyer = destroyer;
    }

    /**
     * Returns the destroying entity
     *
     * @return destroying entity
     */

    public Entity getDestroyer() {
        return destroyer;
    }
}
