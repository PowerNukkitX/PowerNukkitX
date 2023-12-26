package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGreenConcretePowder extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenConcretePowder(BlockState blockstate) {
        super(blockstate);
    }
}