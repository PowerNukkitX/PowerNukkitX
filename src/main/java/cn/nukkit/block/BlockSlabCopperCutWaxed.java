package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockSlabCopperCutWaxed extends BlockSlabCopperCut {


    public BlockSlabCopperCutWaxed() {
        this(0);
    }


    public BlockSlabCopperCutWaxed(int meta) {
        super(meta, WAXED_DOUBLE_CUT_COPPER_SLAB);
    }

    @Override
    public int getId() {
        return WAXED_CUT_COPPER_SLAB;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}
