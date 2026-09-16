package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoplarStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION, CommonBlockProperties.CORNER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoplarStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Poplar Wood Stairs";
    }
}
