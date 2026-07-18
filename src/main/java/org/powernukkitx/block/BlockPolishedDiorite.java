package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDiorite extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_DIORITE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDiorite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDiorite(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}