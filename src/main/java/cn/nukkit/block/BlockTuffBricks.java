package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Tuff Bricks";
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
    public boolean canHarvestWithHand() {
        return false;
    }
}