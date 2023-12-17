package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockMediumAmethystBud extends BlockAmethystBud {
    @Override
    protected String getNamePrefix() {
        return "Medium";
    }

    @Override
    public int getId() {
        return MEDIUM_AMETHYST_BUD;
    }

    @Override
    public int getLightLevel() {
        return 2;
    }
}
