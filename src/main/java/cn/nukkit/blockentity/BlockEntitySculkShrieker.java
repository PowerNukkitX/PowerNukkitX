package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Kevims KCodeYT
 */


public class BlockEntitySculkShrieker extends BlockEntity {


    public BlockEntitySculkShrieker(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.SCULK_SHRIEKER;
    }

}
