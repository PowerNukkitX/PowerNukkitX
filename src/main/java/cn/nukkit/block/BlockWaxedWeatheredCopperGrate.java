package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_weathered_copper_grate");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}