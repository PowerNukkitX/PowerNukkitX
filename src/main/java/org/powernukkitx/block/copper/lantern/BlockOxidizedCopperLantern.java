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
public class BlockOxidizedCopperLantern extends BlockCopperLantern {
    public static final BlockProperties PROPERTIES = new BlockProperties(OXIDIZED_COPPER_LANTERN, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCopperLantern() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCopperLantern(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Oxidized Copper Lantern";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}
