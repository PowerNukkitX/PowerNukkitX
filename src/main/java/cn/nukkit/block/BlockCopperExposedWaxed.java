package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockCopperExposedWaxed extends BlockCopperExposed {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperExposedWaxed( ) {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper";
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean isWaxed() {
        return true;
    }
}
