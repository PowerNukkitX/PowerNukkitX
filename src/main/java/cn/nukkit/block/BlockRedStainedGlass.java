package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockRedStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}