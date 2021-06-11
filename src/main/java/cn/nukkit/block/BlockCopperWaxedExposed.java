package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockCopperWaxedExposed extends BlockCopper {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperWaxedExposed( ) {

    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper";
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER;
    }
}
