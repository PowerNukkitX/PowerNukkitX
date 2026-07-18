package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMud extends BlockSolid implements Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(MUD);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.5)
            .resistance(0.5)
            .toolType(ItemTool.TYPE_SHOVEL)
            .build();

    public BlockMud() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMud(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Mud";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    }
