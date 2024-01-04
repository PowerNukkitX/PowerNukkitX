package cn.nukkit.event.vehicle;


import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

public class VehicleCreateEvent extends VehicleEvent implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public VehicleCreateEvent(Entity vehicle) {
        super(vehicle);
    }

}
