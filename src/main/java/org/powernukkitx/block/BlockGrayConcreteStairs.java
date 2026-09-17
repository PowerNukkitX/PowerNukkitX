package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGrayConcreteStairs extends BlockConcreteStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_concrete_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION, CommonBlockProperties.CORNER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayConcreteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayConcreteStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Gray Concrete Stairs";
    }

}

