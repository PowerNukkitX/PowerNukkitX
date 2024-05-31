package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;


public class BlockEntityDecoratedPot extends BlockEntitySpawnable {
    /**
     * @deprecated 
     */
    
    public BlockEntityDecoratedPot(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.DECORATED_POT;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putList("sherds",namedTag.getList("sherds"));
    }
}
