package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.DoublePlantType;
import org.jetbrains.annotations.NotNull;

public class BlockSunflower extends BlockDoublePlant {
    public static final BlockProperties PROPERTIES = new BlockProperties(SUNFLOWER, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSunflower() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSunflower(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DoublePlantType getDoublePlantType() {
        return DoublePlantType.SUNFLOWER;
    }
}