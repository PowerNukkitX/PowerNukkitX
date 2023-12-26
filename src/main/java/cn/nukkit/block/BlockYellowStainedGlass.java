package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockYellowStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}