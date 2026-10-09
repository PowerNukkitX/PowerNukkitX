package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.tags.BlockTags;

import java.util.Set;

public class BlockWhiteWoolStairs extends BlockWoolStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_wool_stairs", Set.of(BlockTags.PNX_WOOL), CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION, CommonBlockProperties.CORNER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteWoolStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteWoolStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    protected String getWoolName() {
        return "White";
    }

}

