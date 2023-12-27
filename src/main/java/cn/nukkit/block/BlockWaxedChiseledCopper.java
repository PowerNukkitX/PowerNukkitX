package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_chiseled_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}