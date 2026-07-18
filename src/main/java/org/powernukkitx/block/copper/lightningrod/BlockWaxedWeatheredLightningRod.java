package org.powernukkitx.block.copper.lightningrod;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWaxedWeatheredLightningRod extends BlockWaxedLightningRod {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_WEATHERED_LIGHTNING_ROD, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredLightningRod() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredLightningRod(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Weathered Lightning Rod";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}
