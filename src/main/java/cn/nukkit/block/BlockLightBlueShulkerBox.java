package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlueShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}