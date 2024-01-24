package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDripstoneBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(DRIPSTONE_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDripstoneBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDripstoneBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Dripstone Block";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 1;
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

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}