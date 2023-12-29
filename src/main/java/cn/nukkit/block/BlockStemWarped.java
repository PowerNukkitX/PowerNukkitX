package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;


public class BlockStemWarped extends BlockStem {


    public BlockStemWarped() {
        this(0);
    }


    public BlockStemWarped(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return WARPED_STEM;
    }

    @Override
    public String getName() {
        return "Warped Stem";
    }


    @Override
    public BlockState getStrippedState() {
        return getBlockState().withBlockId(STRIPPED_WARPED_STEM);
    }

}
