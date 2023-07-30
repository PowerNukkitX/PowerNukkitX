package cn.nukkit.event.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

public class VehicleMoveEvent extends VehicleEvent {

    private final Location from;
    private final Location to;

    public VehicleMoveEvent(Entity vehicle, Location from, Location to) {
        super(vehicle);
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }
}
