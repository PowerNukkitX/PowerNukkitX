package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockPinkTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}