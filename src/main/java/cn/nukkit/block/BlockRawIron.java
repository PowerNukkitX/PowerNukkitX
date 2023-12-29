package cn.nukkit.block;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */


public class BlockRawIron extends BlockRaw {


    public BlockRawIron() {
        this(0);
    }


    public BlockRawIron(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Block of Raw Iron";
    }

    @Override
    public int getId() {
        return RAW_IRON_BLOCK;
    }

}
