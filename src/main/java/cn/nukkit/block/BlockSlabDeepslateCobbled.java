package cn.nukkit.block;

/**
 * @author joserobjr
 * @since 2021-06-15
 */


public class BlockSlabDeepslateCobbled extends BlockSlab {


    public BlockSlabDeepslateCobbled() {
        this(0);
    }


    public BlockSlabDeepslateCobbled(int meta) {
        super(meta, COBBLED_DEEPSLATE_DOUBLE_SLAB);
    }

    @Override
    public int getId() {
        return COBBLED_DEEPSLATE_SLAB;
    }


    @Override
    public String getSlabName() {
        return "Cobbled Deepslate";
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId() == slab.getId();
    }
}
