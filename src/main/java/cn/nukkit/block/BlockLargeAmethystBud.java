package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
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
