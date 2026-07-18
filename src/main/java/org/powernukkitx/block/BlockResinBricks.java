package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockResinBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(RESIN_BRICKS);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockResinBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockResinBricks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

}