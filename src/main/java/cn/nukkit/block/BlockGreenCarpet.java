package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGreenCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenCarpet(BlockState blockstate) {
        super(blockstate);
    }
}