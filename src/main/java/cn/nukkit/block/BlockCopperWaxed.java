package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockCopperWaxed extends BlockCopper {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Block of Copper";
    }

    @Override
    public int getId() {
        return WAXED_COPPER;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean isWaxed() {
        return true;
    }
}
