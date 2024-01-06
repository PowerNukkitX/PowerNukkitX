package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockEmeraldBlock extends BlockSolid {
    
    public static final BlockProperties PROPERTIES = new BlockProperties(EMERALD_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEmeraldBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEmeraldBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Emerald Block";
    }

    @Override
    public double getHardness() {
        return 5;
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
        return ItemTool.TIER_IRON;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}