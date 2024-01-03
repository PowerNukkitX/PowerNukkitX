package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

public class ProjectileLaunchEvent extends EntityEvent implements Cancellable {
    @Getter
    private static final HandlerList handlers = new HandlerList();

    protected Entity shooter;

    @Deprecated //保留这个方法，兼容nk插件
    public ProjectileLaunchEvent(EntityProjectile entity) {
        this(entity, entity.shootingEntity);
    }

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
