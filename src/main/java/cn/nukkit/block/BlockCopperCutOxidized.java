package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockCopperCutOxidized extends BlockCopperCut {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperCutOxidized() {
        // Does nothing
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getId() {
        return OXIDIZED_CUT_COPPER;
    }
}
