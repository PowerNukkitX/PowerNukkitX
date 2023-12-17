package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;


public class BlockStemCrimson extends BlockStem {


    public BlockStemCrimson() {
        this(0);
    }


    public BlockStemCrimson(int meta) {
        super(meta);
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
        return getCurrentState().withBlockId(STRIPPED_CRIMSON_STEM);
    }

}
