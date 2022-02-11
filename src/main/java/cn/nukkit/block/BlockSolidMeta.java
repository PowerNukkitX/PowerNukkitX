package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public abstract class BlockSolidMeta extends BlockMeta {
    protected BlockSolidMeta(int meta) {
        super(meta);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
