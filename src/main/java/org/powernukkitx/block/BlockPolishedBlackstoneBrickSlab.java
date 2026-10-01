package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneBrickSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneBrickSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneBrickSlab(BlockState blockstate) {
        super(blockstate, POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Polished Blackstone";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

}