package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockCopperWeatheredWaxed extends BlockCopperWeathered {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperWeatheredWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper";
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean isWaxed() {
        return true;
    }
}
