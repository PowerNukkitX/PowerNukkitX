package org.powernukkitx.block.copper.lantern;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWeatheredCopperLantern extends BlockCopperLantern {
    public static final BlockProperties PROPERTIES = new BlockProperties(WEATHERED_COPPER_LANTERN, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCopperLantern() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCopperLantern(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Weathered Copper Lantern";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}
