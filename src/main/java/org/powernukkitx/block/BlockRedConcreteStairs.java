package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedConcreteStairs extends BlockConcreteStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_concrete_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION, CommonBlockProperties.CORNER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedConcreteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedConcreteStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Red Concrete Stairs";
    }

}

