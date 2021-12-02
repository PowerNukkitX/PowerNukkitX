package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockPistonBase;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.math.BlockFace;

import java.util.ArrayList;
import java.util.List;

@PowerNukkitOnly
public class BlockPistonEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @PowerNukkitOnly
    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockFace direction;
    private final List<Block> blocks;
    private final List<Block> destroyedBlocks;
    private final boolean extending;

    @PowerNukkitOnly
    public BlockPistonEvent(BlockPistonBase piston, BlockFace direction, List<Block> blocks, List<Block> destroyedBlocks, boolean extending) {
        super(piston);
        this.direction = direction;
        this.blocks = blocks;
        this.destroyedBlocks = destroyedBlocks;
        this.extending = extending;
    }

    @PowerNukkitOnly
    public BlockFace getDirection() {
        return direction;
    }

    @PowerNukkitOnly
    public List<Block> getBlocks() {
        return new ArrayList<>(blocks);
    }

    @PowerNukkitOnly
    public List<Block> getDestroyedBlocks() {
        return destroyedBlocks;
    }

    @PowerNukkitOnly
    public boolean isExtending() {
        return extending;
    }

    @Override
    public BlockPistonBase getBlock() {
        return (BlockPistonBase) super.getBlock();
    }
}
