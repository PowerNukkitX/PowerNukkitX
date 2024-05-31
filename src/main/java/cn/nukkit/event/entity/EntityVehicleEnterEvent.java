package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class EntityVehicleEnterEvent extends EntityEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity vehicle;
    /**
     * @deprecated 
     */
    

    public EntityVehicleEnterEvent(Entity entity, Entity vehicle) {
        this.entity = entity;
        this.vehicle = vehicle;
    }

    public Entity getVehicle() {
        return vehicle;
    }

}
