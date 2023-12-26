package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCamera extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:camera");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCamera() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCamera(BlockState blockstate) {
        super(blockstate);
    }
}