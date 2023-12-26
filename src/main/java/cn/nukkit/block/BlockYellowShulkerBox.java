package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockYellowShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}