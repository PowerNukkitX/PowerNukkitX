package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCrackedNetherBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRACKED_NETHER_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrackedNetherBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedNetherBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cracked Nether Bricks";
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
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}