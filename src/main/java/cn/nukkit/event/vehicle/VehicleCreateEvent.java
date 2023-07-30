package cn.nukkit.event.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;

public class VehicleCreateEvent extends VehicleEvent implements Cancellable {

    public VehicleCreateEvent(Entity vehicle) {
        super(vehicle);
    }
}
