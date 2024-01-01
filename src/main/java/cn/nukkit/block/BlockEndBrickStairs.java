package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockEndBrickStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:end_brick_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEndBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEndBrickStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 9;
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
    public String getName() {
        return "End Stone Brick Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}