package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockPinkCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkCarpet(BlockState blockstate) {
        super(blockstate);
    }
}