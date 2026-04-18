package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;

/**
 * @author Kevims KCodeYT
 */


public class BlockEntitySculkShrieker extends BlockEntity {


    public BlockEntitySculkShrieker(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.SCULK_SHRIEKER;
    }

}
