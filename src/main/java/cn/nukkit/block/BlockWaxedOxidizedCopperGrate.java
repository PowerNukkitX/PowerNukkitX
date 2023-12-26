package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_oxidized_copper_grate");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}