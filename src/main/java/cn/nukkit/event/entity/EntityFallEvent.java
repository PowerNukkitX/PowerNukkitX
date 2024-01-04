package cn.nukkit.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

public class EntityFallEvent extends EntityEvent implements Cancellable {
    @Getter
    private static final HandlerList handlers = new HandlerList();

    private Block blockFallOn;
    private float fallDistance;

    public EntityFallEvent(Entity entity, Block blockFallOn, float fallDistance) {
        this.entity = entity;
        this.blockFallOn = blockFallOn;
        this.fallDistance = fallDistance;
    }

    public Block getBlockFallOn() {
        return blockFallOn;
    }

    public float getFallDistance() {
        return fallDistance;
    }

    public void setFallDistance(float distance) {
        this.fallDistance = distance;
    }
}
