package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}