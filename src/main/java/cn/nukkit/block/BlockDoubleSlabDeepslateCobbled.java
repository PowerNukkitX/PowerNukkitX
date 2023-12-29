package cn.nukkit.block;

/**
 * @author joserobjr
 * @since 2021-06-15
 */


public class BlockDoubleSlabDeepslateCobbled extends BlockDoubleSlabBase {


    public BlockDoubleSlabDeepslateCobbled() {
        this(0);
    }


    public BlockDoubleSlabDeepslateCobbled(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return COBBLED_DEEPSLATE_DOUBLE_SLAB;
    }


    @Override
    public String getSlabName() {
        return "Cobbled Deepslate";
    }


    @Override
    public int getSingleSlabId() {
        return COBBLED_DEEPSLATE_SLAB;
    }
}
