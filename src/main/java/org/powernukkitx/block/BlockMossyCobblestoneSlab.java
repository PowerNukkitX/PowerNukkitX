package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMossyCobblestoneSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOSSY_COBBLESTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockMossyCobblestoneSlab(BlockState blockState) {
        super(blockState, MOSSY_COBBLESTONE_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Mossy Cobblestone";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return false;
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}
