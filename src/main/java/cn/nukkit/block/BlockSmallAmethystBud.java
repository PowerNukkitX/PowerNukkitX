package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
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
