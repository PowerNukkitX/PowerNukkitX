package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_PLANKS);
    public static final BlockDefinition DEFINITION = BlockPlanks.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(3)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(-1)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedPlanks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Warped Planks";
    }

}