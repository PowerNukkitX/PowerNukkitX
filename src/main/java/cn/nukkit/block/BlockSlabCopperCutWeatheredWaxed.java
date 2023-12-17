package cn.nukkit.block;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockSlabCopperCutWeatheredWaxed extends BlockSlabCopperCutWeathered {


    public BlockSlabCopperCutWeatheredWaxed() {
        this(0);
    }


    public BlockSlabCopperCutWeatheredWaxed(int meta) {
        super(meta, WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB);
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_CUT_COPPER_SLAB;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}
