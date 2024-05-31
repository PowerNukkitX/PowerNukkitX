package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class BlockIgniteEvent extends BlockEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block source;
    private final Entity entity;
    private final BlockIgniteCause cause;
    /**
     * @deprecated 
     */
    

    public BlockIgniteEvent(Block block, Block source, Entity entity, BlockIgniteCause cause) {
        super(block);
        this.source = source;
        this.entity = entity;
        this.cause = cause;
    }

    public Block getSource() {
        return source;
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockIgniteCause getCause() {
        return cause;
    }

    public enum BlockIgniteCause {
        EXPLOSION,
        FIREBALL,
        FLINT_AND_STEEL,
        LAVA,
        LIGHTNING,
        SPREAD
    }
}
