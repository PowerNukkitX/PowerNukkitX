package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorMangrove extends BlockTrapdoor {
    @PowerNukkitOnly
    public BlockTrapdoorMangrove() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockTrapdoorMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Mangrove Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWNISH_RED;
    }
}
