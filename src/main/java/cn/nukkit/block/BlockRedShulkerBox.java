package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockRedShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}