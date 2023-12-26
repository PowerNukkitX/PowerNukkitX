package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}