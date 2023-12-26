package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBrownConcretePowder extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownConcretePowder(BlockState blockstate) {
        super(blockstate);
    }
}