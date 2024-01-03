package cn.nukkit.event.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

/**
 * Is called when an vehicle gets destroyed
 */
public class VehicleDestroyEvent extends VehicleEvent implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    /**
     * Constructor for the VehicleDestroyEvent
     *
     * @param vehicle the destroyed vehicle
     */
    public VehicleDestroyEvent(final Entity vehicle) {
        super(vehicle);
    }

}
