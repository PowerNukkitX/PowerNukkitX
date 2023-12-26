package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHardGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:hard_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHardGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHardGlass(BlockState blockstate) {
        super(blockstate);
    }
}