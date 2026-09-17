package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLimeConcreteStairs extends BlockConcreteStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_concrete_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION, CommonBlockProperties.CORNER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeConcreteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeConcreteStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Lime Concrete Stairs";
    }

}

