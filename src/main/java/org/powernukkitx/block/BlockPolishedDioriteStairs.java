package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDioriteStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_DIORITE_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDioriteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDioriteStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Polished Diorite Stairs";
    }

    @Override
    public double getHardness() {
        return 1.5;
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
    public boolean canHarvestWithHand() {
        return false;
    }
}