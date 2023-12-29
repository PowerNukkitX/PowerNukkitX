package cn.nukkit.block;

/**
 * @author joserobjr
 * @since 2021-06-15
 */


public class BlockStairsCopperCutWaxed extends BlockStairsCopperCut {


    public BlockStairsCopperCutWaxed() {
        this(0);
    }


    public BlockStairsCopperCutWaxed(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return WAXED_CUT_COPPER_STAIRS;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}
