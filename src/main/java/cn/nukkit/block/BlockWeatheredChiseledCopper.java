package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:weathered_chiseled_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}