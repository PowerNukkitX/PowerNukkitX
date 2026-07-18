package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockNetherBrickSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    public BlockNetherBrickSlab(BlockState blockState) {
        super(blockState, NETHER_BRICK_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return "Nether Brick";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(this.getId());
    }

    }
