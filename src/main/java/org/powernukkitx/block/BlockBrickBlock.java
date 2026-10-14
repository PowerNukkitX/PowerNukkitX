package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockBrickBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(BRICK_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrickBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrickBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

}