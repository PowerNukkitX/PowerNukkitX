package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlueTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}