package org.powernukkitx.block.copper.chain;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWaxedExposedCopperChain extends BlockWaxedCopperChain {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_EXPOSED_COPPER_CHAIN, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCopperChain() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCopperChain(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Chain";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}
