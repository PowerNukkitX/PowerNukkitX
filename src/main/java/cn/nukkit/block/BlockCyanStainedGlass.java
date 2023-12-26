package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCyanStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}