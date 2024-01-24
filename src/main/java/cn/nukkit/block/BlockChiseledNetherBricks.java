package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockChiseledNetherBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_NETHER_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledNetherBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledNetherBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Chiseled Nether Bricks";
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