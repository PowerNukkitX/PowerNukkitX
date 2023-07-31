package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockTrapdoor;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
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
}
