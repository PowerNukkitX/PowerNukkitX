package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockTintedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:tinted_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTintedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTintedGlass(BlockState blockstate) {
        super(blockstate);
    }
}