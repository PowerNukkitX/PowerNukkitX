package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;

/**
 * @author GoodLucky777
 */
public class BlockEntityEndPortal extends BlockEntitySpawnable {

    public BlockEntityEndPortal(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }
    
    @Override
    public boolean isBlockEntityValid() {
        return this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) == Block.END_PORTAL;
    }
}
