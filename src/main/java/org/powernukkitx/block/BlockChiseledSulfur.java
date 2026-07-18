package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockChiseledSulfur extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_SULFUR);
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

    public BlockChiseledSulfur() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledSulfur(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}