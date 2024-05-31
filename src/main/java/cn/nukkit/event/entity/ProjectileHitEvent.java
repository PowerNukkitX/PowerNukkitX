package cn.nukkit.event.entity;

import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.MovingObjectPosition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ProjectileHitEvent extends EntityEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private MovingObjectPosition movingObjectPosition;
    /**
     * @deprecated 
     */
    

    public ProjectileHitEvent(EntityProjectile entity) {
        this(entity, null);
    }
    /**
     * @deprecated 
     */
    

    public ProjectileHitEvent(EntityProjectile entity, MovingObjectPosition movingObjectPosition) {
        this.entity = entity;
        this.movingObjectPosition = movingObjectPosition;
    }

    public MovingObjectPosition getMovingObjectPosition() {
        return movingObjectPosition;
    }
    /**
     * @deprecated 
     */
    

    public void setMovingObjectPosition(MovingObjectPosition movingObjectPosition) {
        this.movingObjectPosition = movingObjectPosition;
    }

}
