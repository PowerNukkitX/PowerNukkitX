package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCyanWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanWool(BlockState blockstate) {
        super(blockstate);
    }
}