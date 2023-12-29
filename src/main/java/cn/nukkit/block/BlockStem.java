package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import org.jetbrains.annotations.NotNull;


public abstract class BlockStem extends BlockLog {


    public BlockStem(BlockState blockstate) {
        super(blockstate);
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PILLAR_PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 2;
    }
}
