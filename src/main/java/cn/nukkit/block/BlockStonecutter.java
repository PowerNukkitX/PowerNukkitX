package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockStonecutter extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(STONECUTTER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStonecutter() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStonecutter(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Stonecutter";
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 17.5;
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
    public int getWaterloggingLevel() {
        return 1;
    }
}
