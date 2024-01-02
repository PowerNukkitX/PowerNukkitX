package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCopper extends BlockCopperBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_COPPER);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}