package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;

public abstract class BlockMushroomBlock extends BlockSolid {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.2)
            .resistance(0.2)
            .toolType(ItemTool.TYPE_AXE)
            .build();

    public BlockMushroomBlock(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockMushroomBlock(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    }
