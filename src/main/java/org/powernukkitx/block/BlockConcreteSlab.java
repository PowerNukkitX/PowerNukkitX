package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;
import org.powernukkitx.item.ItemTool;

public abstract class BlockConcreteSlab extends BlockSlab {
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    protected BlockConcreteSlab(BlockState blockState, String doubleSlab) {
        super(blockState, doubleSlab, DEFINITION);
    }
}
