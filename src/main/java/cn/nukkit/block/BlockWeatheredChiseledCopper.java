package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWeatheredChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WEATHERED_CHISELED_COPPER);

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