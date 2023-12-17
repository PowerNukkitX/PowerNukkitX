package cn.nukkit.block;

import cn.nukkit.math.BlockFace;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockSolid extends Block {

    protected BlockSolid() {
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
