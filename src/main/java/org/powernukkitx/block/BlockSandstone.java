package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSandstone extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(SANDSTONE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.8)
            .resistance(0.8)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSandstone(BlockState state) {
        super(state, DEFINITION);
    }

    public BlockSandstone(BlockState state, BlockDefinition definition) {
        super(state, definition);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
