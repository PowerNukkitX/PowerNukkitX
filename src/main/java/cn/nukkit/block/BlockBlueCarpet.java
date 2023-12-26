package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBlueCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueCarpet(BlockState blockstate) {
        super(blockstate);
    }
}