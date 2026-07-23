package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMudBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(MUD_BRICKS);
    public static final BlockDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .hardness(3)
            .resistance(1.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMudBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMudBricks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Mud Bricks";
    }

    }