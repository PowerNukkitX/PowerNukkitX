package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class ProjectileLaunchEvent extends EntityEvent implements Cancellable {
    protected Entity shooter;
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Deprecated //保留这个方法，兼容nk插件
    public ProjectileLaunchEvent(EntityProjectile entity) {
        this(entity, entity.shootingEntity);
    }

    @PowerNukkitXDifference(info = "事件增强 | ProjectileLaunchEvent improve")
    public ProjectileLaunchEvent(EntityProjectile entity, Entity shooter) {
        this.shooter = shooter;
        this.entity = entity;
    }

    @Override
    public EntityProjectile getEntity() {
        return (EntityProjectile) this.entity;
    }

    @PowerNukkitXOnly
    @Since("1.19.31-r1")
    public Entity getShooter() {
        return this.shooter;
    }
}
