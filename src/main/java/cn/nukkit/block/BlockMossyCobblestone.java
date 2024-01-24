package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMossyCobblestone extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOSSY_COBBLESTONE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMossyCobblestone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMossyCobblestone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mossy Cobblestone";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
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