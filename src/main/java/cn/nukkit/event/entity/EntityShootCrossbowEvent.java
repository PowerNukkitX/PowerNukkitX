package cn.nukkit.event.entity;

import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * @author GoodLucky777, Superice666
 */
public class EntityShootCrossbowEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item crossbow;

    private EntityProjectile[] projectiles;

    public EntityShootCrossbowEvent(EntityLiving shooter, Item crossbow, EntityProjectile... projectiles) {
        this.entity = shooter;
        this.crossbow = crossbow;
        this.projectiles = projectiles;
    }

    @Override
    public EntityLiving getEntity() {
        return (EntityLiving) this.entity;
    }

    public Item getCrossbow() {
        return this.crossbow;
    }

    public EntityProjectile getProjectile(int array) {
        return this.projectiles[array];
    }

    public EntityProjectile[] getProjectiles() {
        return this.projectiles;
    }

    public int getProjectilesCount() {
        return this.projectiles.length;
    }

    public void setProjectile(EntityProjectile projectile, int array) {
        if (projectile != this.projectiles[array]) {
            if (this.projectiles[array].getViewers().size() == 0) {
                this.projectiles[array].kill();
                this.projectiles[array].close();
            }
            this.projectiles[array] = projectile;
        }
    }

    public void setProjectiles(EntityProjectile[] projectiles) {
        for (int i = 0; i < this.projectiles.length; i++) {
            if (projectiles[i] != this.projectiles[i]) {
                if (this.projectiles[i].getViewers().size() == 0) {
                    this.projectiles[i].kill();
                    this.projectiles[i].close();
                }
                this.projectiles[i] = projectiles[i];
            }
        }
    }

    public void killProjectiles() {
        for (int i = 0; i < this.projectiles.length; i++) {
            this.projectiles[i].kill();
        }
    }
}