package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeConcreteStairs extends BlockConcreteStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_concrete_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION, CommonBlockProperties.CORNER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeConcreteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeConcreteStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Orange Concrete Stairs";
    }

}

