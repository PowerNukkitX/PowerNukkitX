package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;


public class BlockHyphaeWarped extends BlockStem {


    public BlockHyphaeWarped() {
        this(0);
    }


    public BlockHyphaeWarped(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return WARPED_HYPHAE;
    }

    @Override
    public String getName() {
        return "Warped Hyphae";
    }


    @Override
    public BlockState getStrippedState() {
        return getBlockState().withBlockId(STRIPPED_WARPED_HYPHAE);
    }

}
