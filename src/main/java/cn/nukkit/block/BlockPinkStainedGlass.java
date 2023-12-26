package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockPinkStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}