package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockTrapdoor;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooTrapdoor extends BlockTrapdoor {
    public BlockBambooTrapdoor() {
        this(0);
    }

    public BlockBambooTrapdoor(int meta) {
        super(meta);
    }

    public int getId() {
        return BAMBOO_TRAPDOOR;
    }

    public String getName() {
        return "Bamboo Trapdoor";
    }
}
