package org.powernukkitx.event.entity;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;

/**
 * @author Box (Nukkit Project)
 */
public class EntityShootBowEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item bow;

    private EntityProjectile projectile;

    private double force;

    public EntityShootBowEvent(EntityLiving shooter, Item bow, EntityProjectile projectile, double force) {
        this.entity = shooter;
        this.bow = bow;
        this.projectile = projectile;
        this.force = force;
    }

    @Override
    public EntityLiving getEntity() {
        return (EntityLiving) this.entity;
    }

    public Item getBow() {
        return this.bow;
    }

    public EntityProjectile getProjectile() {
        return this.projectile;
    }

    public void setProjectile(Entity projectile) {
        if (projectile != this.projectile) {
            if (this.projectile.getViewers().isEmpty()) {
                this.projectile.kill();
                this.projectile.close();
            }
            this.projectile = (EntityProjectile) projectile;
        }
    }

    public double getForce() {
        return this.force;
    }

    public void setForce(double force) {
        this.force = force;
    }
}
