package cn.nukkit.event.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

public class VehicleUpdateEvent extends VehicleEvent {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public VehicleUpdateEvent(Entity vehicle) {
        super(vehicle);
    }

}
