package cn.nukkit.block;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockDoubleSlabCopperCutExposedWaxed extends BlockDoubleSlabCopperCutExposed {


    public BlockDoubleSlabCopperCutExposedWaxed() {
        this(0);
    }


    public BlockDoubleSlabCopperCutExposedWaxed(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public int getSingleSlabId() {
        return WAXED_EXPOSED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}
