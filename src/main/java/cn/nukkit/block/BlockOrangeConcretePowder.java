package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeConcretePowder extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeConcretePowder(BlockState blockstate) {
        super(blockstate);
    }
}