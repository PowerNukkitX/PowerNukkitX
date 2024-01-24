package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCobbledDeepslate extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(COBBLED_DEEPSLATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobbledDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCobbledDeepslate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cobbled Deepslate";
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 6.0;
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