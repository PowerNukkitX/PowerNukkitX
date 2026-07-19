package org.powernukkitx.block.copper.chest;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWeatheredCopperChest extends BlockCopperChest {
    public static final BlockProperties PROPERTIES = new BlockProperties(WEATHERED_COPPER_CHEST, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCopperChest() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCopperChest(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Weathered Copper Chest";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}
