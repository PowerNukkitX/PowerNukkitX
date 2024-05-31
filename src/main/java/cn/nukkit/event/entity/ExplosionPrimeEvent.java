package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * @author Box (Nukkit Project)
 * <p>
 * Called when a entity decides to explode
 */
public class ExplosionPrimeEvent extends EntityEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected double force;
    private boolean blockBreaking;
    /**
     * @deprecated 
     */
    

    public ExplosionPrimeEvent(Entity entity, double force) {
        this.entity = entity;
        this.force = force;
        this.blockBreaking = true;
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
    /**
     * @deprecated 
     */
    

    public boolean isBlockBreaking() {
        return this.blockBreaking;
    }
    /**
     * @deprecated 
     */
    

    public void setBlockBreaking(boolean affectsBlocks) {
        this.blockBreaking = affectsBlocks;
    }
}
