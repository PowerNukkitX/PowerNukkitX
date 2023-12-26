package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockJungleFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:jungle_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleFence(BlockState blockstate) {
        super(blockstate);
    }
}