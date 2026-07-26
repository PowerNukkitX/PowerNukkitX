package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * @author GoodLucky777
 */
public class BlockEntityEndPortal extends BlockEntitySpawnable {

    public BlockEntityEndPortal(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    
    @Override
    public boolean isBlockEntityValid() {
        return this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) == Block.END_PORTAL;
    }
}
