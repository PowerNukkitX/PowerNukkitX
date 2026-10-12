package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedPoplarWood extends BlockWoodStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_POPLAR_WOOD, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedPoplarWood(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.POPLAR;
    }
}
