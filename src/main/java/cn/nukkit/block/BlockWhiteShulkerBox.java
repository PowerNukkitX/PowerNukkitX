package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}