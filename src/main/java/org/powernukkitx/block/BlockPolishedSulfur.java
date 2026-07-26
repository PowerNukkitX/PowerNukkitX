package org.powernukkitx.block;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedSulfur extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_SULFUR);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedSulfur() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedSulfur(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 6;
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