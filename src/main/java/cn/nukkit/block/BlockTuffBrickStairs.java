package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBrickStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_BRICK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBrickStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Tuff Brick Stairs";
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
    public boolean canHarvestWithHand() {
        return false;
    }
}