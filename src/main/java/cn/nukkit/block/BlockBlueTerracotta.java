package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBlueTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}