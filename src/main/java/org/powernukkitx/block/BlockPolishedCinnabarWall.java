package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedCinnabarWall extends BlockWallBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_CINNABAR_WALL, CommonBlockProperties.WALL_CONNECTION_TYPE_EAST, CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH, CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH, CommonBlockProperties.WALL_CONNECTION_TYPE_WEST, CommonBlockProperties.WALL_POST_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedCinnabarWall() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedCinnabarWall(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Polished Cinnabar Wall";
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}