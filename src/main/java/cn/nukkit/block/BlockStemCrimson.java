package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;


public class BlockStemCrimson extends BlockStem {


    public BlockStemCrimson() {
        this(0);
    }


    public BlockStemCrimson(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return CRIMSON_STEM;
    }

    @Override
    public String getName() {
        return "Crimson Stem";
    }


    @Override
    public BlockState getStrippedState() {
        return getBlockState().withBlockId(STRIPPED_CRIMSON_STEM);
    }

}
