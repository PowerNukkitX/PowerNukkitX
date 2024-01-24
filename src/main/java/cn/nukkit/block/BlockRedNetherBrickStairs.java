package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockRedNetherBrickStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_NETHER_BRICK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedNetherBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedNetherBrickStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Red Nether Brick Stairs";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
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