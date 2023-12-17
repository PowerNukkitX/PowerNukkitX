package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;


public class BlockStemWarped extends BlockStem {


    public BlockStemWarped() {
        this(0);
    }


    public BlockStemWarped(int meta) {
        super(meta);
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
        return getCurrentState().withBlockId(STRIPPED_WARPED_STEM);
    }

}
