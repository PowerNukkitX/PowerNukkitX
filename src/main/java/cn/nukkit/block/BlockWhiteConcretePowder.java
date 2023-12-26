package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteConcretePowder extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteConcretePowder(BlockState blockstate) {
        super(blockstate);
    }
}