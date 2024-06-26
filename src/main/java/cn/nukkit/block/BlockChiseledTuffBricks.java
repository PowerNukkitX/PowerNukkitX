package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockChiseledTuffBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_TUFF_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledTuffBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledTuffBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Chiseled Tuff Bricks";
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
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}