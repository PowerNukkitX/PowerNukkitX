package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockRedConcretePowder extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedConcretePowder(BlockState blockstate) {
        super(blockstate);
    }
}