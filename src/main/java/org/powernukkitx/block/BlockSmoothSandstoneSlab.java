package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothSandstoneSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_SANDSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    public BlockSmoothSandstoneSlab(BlockState blockState) {
        super(blockState, SMOOTH_SANDSTONE_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Smooth Sandstone";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(this.getId());
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
}
