package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteCarpet(BlockState blockstate) {
        super(blockstate);
    }
}