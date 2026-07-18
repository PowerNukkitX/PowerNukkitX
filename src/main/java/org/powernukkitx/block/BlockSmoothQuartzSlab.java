package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothQuartzSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_QUARTZ_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    public BlockSmoothQuartzSlab(BlockState blockState) {
        super(blockState, SMOOTH_QUARTZ_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return "Smooth Quartz";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return this.getId().equals(slab.getId());
    }
}