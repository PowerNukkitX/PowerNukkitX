package cn.nukkit.block;

import cn.nukkit.math.BlockFace;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockSolid extends Block {

    public BlockSolid(BlockState blockState) {
        super(blockState);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return true;
    }

}
