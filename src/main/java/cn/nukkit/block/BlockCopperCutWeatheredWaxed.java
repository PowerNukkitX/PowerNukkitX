package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockCopperCutWeatheredWaxed extends BlockCopperCutWeathered {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperCutWeatheredWaxed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Weathered Cut Copper";
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_CUT_COPPER;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean isWaxed() {
        return true;
    }
}
