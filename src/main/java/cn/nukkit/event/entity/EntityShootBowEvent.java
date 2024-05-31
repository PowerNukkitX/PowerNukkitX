package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * @author Box (Nukkit Project)
 */
public class EntityShootBowEvent extends EntityEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item bow;

    private EntityProjectile projectile;

    private double force;
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    

    public void setProjectile(Entity projectile) {
        if (projectile != this.projectile) {
            if (this.projectile.getViewers().isEmpty()) {
                this.projectile.kill();
                this.projectile.close();
            }
            this.projectile = (EntityProjectile) projectile;
        }
    }
    /**
     * @deprecated 
     */
    

    public double getForce() {
        return this.force;
    }
    /**
     * @deprecated 
     */
    

    public void setForce(double force) {
        this.force = force;
    }
}
