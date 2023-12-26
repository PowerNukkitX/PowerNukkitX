package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBlueStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}