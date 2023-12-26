package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}