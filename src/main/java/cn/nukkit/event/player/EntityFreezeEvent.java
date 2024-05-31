package cn.nukkit.event.player;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.entity.EntityEvent;

public class EntityFreezeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    /**
     * @deprecated 
     */
    

    public EntityFreezeEvent(Entity human) {
        this.entity = human;
    }
}
