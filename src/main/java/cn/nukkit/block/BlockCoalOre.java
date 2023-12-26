package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCoalOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:coal_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoalOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoalOre(BlockState blockstate) {
        super(blockstate);
    }
}