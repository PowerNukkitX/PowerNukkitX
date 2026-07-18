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
public class BlockWaxedOxidizedLightningRod extends BlockWaxedLightningRod {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_OXIDIZED_LIGHTNING_ROD, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedLightningRod() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedLightningRod(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Lightning Rod";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}
