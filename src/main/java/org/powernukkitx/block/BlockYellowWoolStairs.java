package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.tags.BlockTags;

import java.util.Set;

public class BlockYellowWoolStairs extends BlockWoolStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_wool_stairs", Set.of(BlockTags.PNX_WOOL), CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION, CommonBlockProperties.CORNER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowWoolStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowWoolStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    protected String getWoolName() {
        return "Yellow";
    }

}

