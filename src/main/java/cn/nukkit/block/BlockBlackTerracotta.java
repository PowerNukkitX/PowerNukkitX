package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBlackTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}