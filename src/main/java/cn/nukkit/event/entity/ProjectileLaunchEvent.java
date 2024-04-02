package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class ProjectileLaunchEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected Entity shooter;

    public ProjectileLaunchEvent(EntityProjectile entity, Entity shooter) {
        this.shooter = shooter;
        this.entity = entity;
    }

    @Override
    public EntityProjectile getEntity() {
        return (EntityProjectile) this.entity;
    }

    public Entity getShooter() {
        return this.shooter;
    }
}
