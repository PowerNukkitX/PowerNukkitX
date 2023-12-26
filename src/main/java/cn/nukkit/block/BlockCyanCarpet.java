package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCyanCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanCarpet(BlockState blockstate) {
        super(blockstate);
    }
}