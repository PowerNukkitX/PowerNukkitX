package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLimeCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeCarpet(BlockState blockstate) {
        super(blockstate);
    }
}