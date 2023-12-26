package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGreenStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}