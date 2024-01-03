package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * @since 15-10-27
 */
public class EntityExplosionPrimeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private double force;
    private boolean blockBreaking;
    private double fireChance;

    public EntityExplosionPrimeEvent(Entity entity, double force) {
        this.entity = entity;
        this.force = force;
        this.blockBreaking = true;
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public boolean isBlockBreaking() {
        return blockBreaking;
    }

    public void setBlockBreaking(boolean blockBreaking) {
        this.blockBreaking = blockBreaking;
    }

    public boolean isIncendiary() {
        return fireChance > 0;
    }

    public void setIncendiary(boolean incendiary) {
        if (!incendiary) {
            fireChance = 0;
        } else if (fireChance <= 0) {
            fireChance = 1.0/3.0;
        }
    }

    public double getFireChance() {
        return fireChance;
    }

    public void setFireChance(double fireChance) {
        this.fireChance = fireChance;
    }
}
