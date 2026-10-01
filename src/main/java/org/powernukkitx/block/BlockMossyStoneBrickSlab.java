package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMossyStoneBrickSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOSSY_STONE_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .hardness(1.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    public BlockMossyStoneBrickSlab(BlockState blockState) {
        super(blockState, MOSSY_STONE_BRICK_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return "Mossy Stone Brick";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return this.getId().equals(slab.getId());
    }

    }