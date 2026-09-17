package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayConcreteStairs extends BlockConcreteStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_concrete_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION, CommonBlockProperties.CORNER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayConcreteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayConcreteStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "LightGray Concrete Stairs";
    }

}

