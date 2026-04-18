package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;

/**
 * @author Kevims KCodeYT
 */


public class BlockEntitySculkCatalyst extends BlockEntity {


    public BlockEntitySculkCatalyst(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.SCULK_CATALYST;
    }

}
