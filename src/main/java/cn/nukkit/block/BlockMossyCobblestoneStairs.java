package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMossyCobblestoneStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOSSY_COBBLESTONE_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMossyCobblestoneStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMossyCobblestoneStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mossy Cobblestone Stairs";
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