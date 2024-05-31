package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityCombustEvent extends EntityEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected int duration;
    /**
     * @deprecated 
     */
    

    public EntityCombustEvent(Entity combustee, int duration) {
        this.entity = combustee;
        this.duration = duration;
    }
    /**
     * @deprecated 
     */
    

    public int getDuration() {
        return duration;
    }
    /**
     * @deprecated 
     */
    

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
