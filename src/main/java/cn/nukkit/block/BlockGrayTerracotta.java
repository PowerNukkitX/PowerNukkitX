package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGrayTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}