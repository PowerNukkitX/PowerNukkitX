package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockGoldBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(GOLD_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGoldBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGoldBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Gold Block";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 30;
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