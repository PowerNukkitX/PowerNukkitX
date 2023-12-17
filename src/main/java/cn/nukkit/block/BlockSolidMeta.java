package cn.nukkit.block;

import cn.nukkit.math.BlockFace;

public abstract class BlockSolidMeta extends BlockMeta {
    protected BlockSolidMeta(int meta) {
        super(meta);
    }


    public BlockSolidMeta(){}

    @Override
    public boolean isSolid() {
        return true;
    }


    @Override
    public boolean isSolid(BlockFace side) {
        return true;
    }

}
