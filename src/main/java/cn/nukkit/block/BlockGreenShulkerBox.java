package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGreenShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}