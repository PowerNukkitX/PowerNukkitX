package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGrayConcretePowder extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayConcretePowder(BlockState blockstate) {
        super(blockstate);
    }
}