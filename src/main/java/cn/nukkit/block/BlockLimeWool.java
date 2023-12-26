package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLimeWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeWool(BlockState blockstate) {
        super(blockstate);
    }
}