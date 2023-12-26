package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleCarpet(BlockState blockstate) {
        super(blockstate);
    }
}