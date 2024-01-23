package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockHardenedClay extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(HARDENED_CLAY);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHardenedClay() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHardenedClay(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1.25;
    }

    @Override
    public double getResistance() {
        return 7;
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