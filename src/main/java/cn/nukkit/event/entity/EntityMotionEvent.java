package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.math.Vector3;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityMotionEvent extends EntityEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Vector3 motion;
    /**
     * @deprecated 
     */
    

    public EntityMotionEvent(Entity entity, Vector3 motion) {
        this.entity = entity;
        this.motion = motion;
    }

    public Vector3 getMotion() {
        return this.motion;
    }
}
