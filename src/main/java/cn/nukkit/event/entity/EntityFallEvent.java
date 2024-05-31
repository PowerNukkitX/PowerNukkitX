package cn.nukkit.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class EntityFallEvent extends EntityEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Block blockFallOn;
    private float fallDistance;
    /**
     * @deprecated 
     */
    

    public EntityFallEvent(Entity entity, Block blockFallOn, float fallDistance) {
        this.entity = entity;
        this.blockFallOn = blockFallOn;
        this.fallDistance = fallDistance;
    }

    public Block getBlockFallOn() {
        return blockFallOn;
    }
    /**
     * @deprecated 
     */
    

    public float getFallDistance() {
        return fallDistance;
    }
    /**
     * @deprecated 
     */
    

    public void setFallDistance(float distance) {
        this.fallDistance = distance;
    }
}
