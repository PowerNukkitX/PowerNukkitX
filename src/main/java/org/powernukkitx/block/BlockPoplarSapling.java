package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockPoplarSapling extends BlockSapling {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_SAPLING, CommonBlockProperties.AGE_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarSapling() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPoplarSapling(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.POPLAR;
    }
}
