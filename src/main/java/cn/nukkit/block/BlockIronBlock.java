package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockIronBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(IRON_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockIronBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockIronBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Iron Block";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}