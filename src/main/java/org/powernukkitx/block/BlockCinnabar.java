package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCinnabar extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(CINNABAR);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCinnabar() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCinnabar(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    }