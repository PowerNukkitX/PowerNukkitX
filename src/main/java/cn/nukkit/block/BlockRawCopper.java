package cn.nukkit.block;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */


public class BlockRawCopper extends BlockRaw {


    public BlockRawCopper() {
        this(0);
    }


    public BlockRawCopper(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Block of Raw Copper";
    }

    @Override
    public int getId() {
        return RAW_COPPER_BLOCK;
    }
}
