package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCutCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:exposed_cut_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCutCopper(BlockState blockstate) {
        super(blockstate);
    }
}