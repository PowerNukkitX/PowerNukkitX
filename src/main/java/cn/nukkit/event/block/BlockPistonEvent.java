package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockPistonBase;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.math.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class BlockPistonEvent extends BlockEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockFace direction;
    private final List<Block> blocks;
    private final List<Block> destroyedBlocks;
    private final boolean extending;
    /**
     * @deprecated 
     */
    

    public BlockPistonEvent(BlockPistonBase piston, BlockFace direction, List<Block> blocks, List<Block> destroyedBlocks, boolean extending) {
        super(piston);
        this.direction = direction;
        this.blocks = blocks;
        this.destroyedBlocks = destroyedBlocks;
        this.extending = extending;
    }

    public BlockFace getDirection() {
        return direction;
    }

    public List<Block> getBlocks() {
        return new ArrayList<>(blocks);
    }

    public List<Block> getDestroyedBlocks() {
        return destroyedBlocks;
    }
    /**
     * @deprecated 
     */
    

    public boolean isExtending() {
        return extending;
    }

    @Override
    public BlockPistonBase getBlock() {
        return (BlockPistonBase) super.getBlock();
    }
}
