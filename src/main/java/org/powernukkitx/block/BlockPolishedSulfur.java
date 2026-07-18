package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedSulfur extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_SULFUR);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedSulfur() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedSulfur(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}