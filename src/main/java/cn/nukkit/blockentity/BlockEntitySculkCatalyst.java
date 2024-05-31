package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Kevims KCodeYT
 */


public class BlockEntitySculkCatalyst extends BlockEntity {
    /**
     * @deprecated 
     */
    


    public BlockEntitySculkCatalyst(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.SCULK_CATALYST;
    }

}
