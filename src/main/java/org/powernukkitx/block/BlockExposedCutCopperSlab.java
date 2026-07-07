package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCutCopperSlab extends BlockCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCutCopperSlab(BlockState blockstate) {
        super(blockstate, EXPOSED_DOUBLE_CUT_COPPER_SLAB);
    }

    protected BlockExposedCutCopperSlab(BlockState blockstate, String doubleSlabId) {
        super(blockstate, doubleSlabId);
    }

    @Override
    @NotNull
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}