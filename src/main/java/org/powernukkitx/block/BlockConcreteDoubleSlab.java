package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;
import org.powernukkitx.item.ItemTool;

public abstract class BlockConcreteDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockDefinition DEFINITION = BlockDoubleSlabBase.DEFINITION.toBuilder()
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .canHarvestWithHand(false)
            .build();

    protected BlockConcreteDoubleSlab(BlockState blockState) {
        super(blockState, DEFINITION);
    }
}
