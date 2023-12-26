package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGrayShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}