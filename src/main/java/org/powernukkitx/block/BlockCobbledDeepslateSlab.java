package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCobbledDeepslateSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(COBBLED_DEEPSLATE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .hardness(3.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobbledDeepslateSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCobbledDeepslateSlab(BlockState blockstate) {
        super(blockstate, COBBLED_DEEPSLATE_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Cobbled Deepslate";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }
}