package org.powernukkitx.event.entity;

import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.MovingObjectPosition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ProjectileHitEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private MovingObjectPosition movingObjectPosition;

    public ProjectileHitEvent(EntityProjectile entity) {
        this(entity, null);
    }

    public ProjectileHitEvent(EntityProjectile entity, MovingObjectPosition movingObjectPosition) {
        this.entity = entity;
        this.movingObjectPosition = movingObjectPosition;
    }

    public MovingObjectPosition getMovingObjectPosition() {
        return movingObjectPosition;
    }

    public void setMovingObjectPosition(MovingObjectPosition movingObjectPosition) {
        this.movingObjectPosition = movingObjectPosition;
    }

}
