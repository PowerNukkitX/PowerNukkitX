package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockFrogLight;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockOchreFrogLight extends BlockFrogLight {
    @Override
    public String getName() {
        return "Ochre FrogLight";
    }

    @Override
    public int getId() {
        return OCHRE_FROGLIGHT;
    }
}
