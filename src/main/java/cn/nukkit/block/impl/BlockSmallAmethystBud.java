package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockAmethystBud;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockSmallAmethystBud extends BlockAmethystBud {
    @Override
    protected String getNamePrefix() {
        return "Small";
    }

    @Override
    public int getId() {
        return SMALL_AMETHYST_BUD;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }
}
