package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_weathered_chiseled_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}