package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

import javax.annotation.Nullable;


public class FarmLandDecayEvent extends BlockEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity entity;
    /**
     * @deprecated 
     */
    

    public FarmLandDecayEvent(@Nullable Entity entity, Block farm) {
        super(farm);
        this.entity = entity;
    }

    public @Nullable Entity getEntity() {
        return entity;
    }
}
