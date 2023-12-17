package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockSlabCopperCutOxidizedWaxed extends BlockSlabCopperCutOxidized {


    public BlockSlabCopperCutOxidizedWaxed() {
        this(0);
    }


    public BlockSlabCopperCutOxidizedWaxed(int meta) {
        super(meta, WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB);
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_CUT_COPPER_SLAB;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}
