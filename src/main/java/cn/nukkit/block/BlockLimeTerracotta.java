package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLimeTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}