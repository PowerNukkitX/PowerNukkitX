package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockFrogLight;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockPearlescentFrogLight extends BlockFrogLight {
    @Override
    public String getName() {
        return "Pearlescent FrogLight";
    }

    @Override
    public int getId() {
        return PEARLESCENT_FROGLIGHT;
    }
}
