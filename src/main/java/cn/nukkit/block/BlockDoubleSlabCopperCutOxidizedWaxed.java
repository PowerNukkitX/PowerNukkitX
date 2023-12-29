package cn.nukkit.block;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockDoubleSlabCopperCutOxidizedWaxed extends BlockDoubleSlabCopperCutOxidized {


    public BlockDoubleSlabCopperCutOxidizedWaxed() {
        this(0);
    }


    public BlockDoubleSlabCopperCutOxidizedWaxed(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public int getSingleSlabId() {
        return WAXED_OXIDIZED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}
