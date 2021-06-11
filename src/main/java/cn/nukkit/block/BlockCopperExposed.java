package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockCopperExposed extends BlockCopper {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperExposed() {

    }

    @Override
    public String getName() {
        return "Exposed Copper";
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER;
    }
}
