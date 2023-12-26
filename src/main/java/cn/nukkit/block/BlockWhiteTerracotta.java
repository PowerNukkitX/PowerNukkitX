package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}