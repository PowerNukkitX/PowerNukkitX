package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockDoubleSlabCopperCutWeatheredWaxed extends BlockDoubleSlabCopperCutWeathered {


    public BlockDoubleSlabCopperCutWeatheredWaxed() {
        this(0);
    }


    public BlockDoubleSlabCopperCutWeatheredWaxed(int meta) {
        super(meta);
    }


    @Override
    public int getSingleSlabId() {
        return WAXED_WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}
