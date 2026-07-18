package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDeepslateSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_DEEPSLATE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .hardness(3.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDeepslateSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDeepslateSlab(BlockState blockstate) {
        super(blockstate, POLISHED_DEEPSLATE_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Polished Deepslate";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

}