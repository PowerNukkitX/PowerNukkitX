package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.utils.DyeColor;

public abstract class BlockWool extends BlockSolid {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.8)
            .resistance(0.8)
            .toolType(ItemTool.TYPE_SHEARS)
            .burnChance(30)
            .burnAbility(60)
            .build();

    public BlockWool(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockWool(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return getDyeColor().getName() + " Wool";
    }

    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
