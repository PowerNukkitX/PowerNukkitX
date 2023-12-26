package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLimeShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}