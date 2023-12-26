package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlueConcretePowder extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueConcretePowder(BlockState blockstate) {
        super(blockstate);
    }
}