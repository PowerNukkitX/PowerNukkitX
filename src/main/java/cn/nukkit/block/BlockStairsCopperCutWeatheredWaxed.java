package cn.nukkit.block;

/**
 * @author joserobjr
 * @since 2021-06-15
 */


public class BlockStairsCopperCutWeatheredWaxed extends BlockStairsCopperCutWeathered {


    public BlockStairsCopperCutWeatheredWaxed() {
        this(0);
    }


    public BlockStairsCopperCutWeatheredWaxed(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_CUT_COPPER_STAIRS;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}
