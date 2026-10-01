package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;
import org.powernukkitx.item.ItemTool;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public abstract class BlockConcrete extends BlockSolid {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.8)
            .resistance(9)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    public BlockConcrete(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockConcrete(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }
}
