package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockLargeAmethystBud extends BlockAmethystBud {
    @Override
    protected String getNamePrefix() {
        return "Large";
    }

    @Override
    public int getId() {
        return LARGE_AMETHYST_BUD;
    }

    @Override
    public int getLightLevel() {
        return 4;
    }
}
