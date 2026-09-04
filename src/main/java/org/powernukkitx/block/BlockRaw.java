package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */
public abstract class BlockRaw extends BlockSolid {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_STONE)
            .build();

    public BlockRaw(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockRaw(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
