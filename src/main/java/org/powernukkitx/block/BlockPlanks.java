package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;
import org.powernukkitx.item.ItemTool;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockPlanks extends BlockSolid {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2)
            .resistance(15)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(5)
            .burnAbility(20)
            .build();

    public BlockPlanks(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockPlanks(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }
}
