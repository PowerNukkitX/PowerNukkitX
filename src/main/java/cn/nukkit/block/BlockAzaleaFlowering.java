package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 13/06/2021
 */


public class BlockAzaleaFlowering extends BlockAzalea {


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
