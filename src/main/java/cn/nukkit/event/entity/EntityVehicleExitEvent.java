package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;

public class EntityVehicleExitEvent extends EntityEvent implements Cancellable {

    private final Entity vehicle;

    public EntityVehicleExitEvent(Entity entity, Entity vehicle) {
        this.entity = entity;
        this.vehicle = vehicle;
    }

    public Entity getVehicle() {
        return vehicle;
    }
}
