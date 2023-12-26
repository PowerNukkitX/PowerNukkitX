package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_weathered_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCopper(BlockState blockstate) {
        super(blockstate);
    }
}