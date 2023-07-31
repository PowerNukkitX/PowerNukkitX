package cn.nukkit.block;

import cn.nukkit.block.Block;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockTransparent extends Block {

    @Override
    public boolean isTransparent() {
        return true;
    }
}
