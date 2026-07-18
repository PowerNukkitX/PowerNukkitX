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
public class BlockExposedCopperChest extends BlockCopperChest {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_COPPER_CHEST, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperChest() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperChest(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Exposed Copper Chest";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}
