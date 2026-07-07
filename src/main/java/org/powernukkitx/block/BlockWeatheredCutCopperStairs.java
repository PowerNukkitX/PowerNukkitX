package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCutCopperStairs extends BlockCutCopperStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(WEATHERED_CUT_COPPER_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCutCopperStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCutCopperStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}