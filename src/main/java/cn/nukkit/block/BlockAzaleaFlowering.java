package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 13/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockAzaleaFlowering extends BlockAzalea {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockAzaleaFlowering() {
    }

    @Override
    public String getName() {
        return "Flowering Azalea";
    }

    @Override
    public int getId() {
        return FLOWERING_AZALEA;
    }
}
