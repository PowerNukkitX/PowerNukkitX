package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockFenceBase;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockFenceCherry extends BlockFenceBase {
    public BlockFenceCherry() {
        this(0);
    }

    public BlockFenceCherry(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Fence";
    }

    @Override
    public int getId() {
        return CHERRY_FENCE;
    }
}
