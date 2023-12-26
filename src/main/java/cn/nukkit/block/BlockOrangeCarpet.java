package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeCarpet(BlockState blockstate) {
        super(blockstate);
    }
}