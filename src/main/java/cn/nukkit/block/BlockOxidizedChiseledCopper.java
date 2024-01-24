package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOxidizedChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(OXIDIZED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}