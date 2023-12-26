package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:weathered_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCopper(BlockState blockstate) {
        super(blockstate);
    }
}