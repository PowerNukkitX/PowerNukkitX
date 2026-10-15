package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockResin extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(RESIN_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0)
            .resistance(0)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockResin() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockResin(BlockState state) {
        super(state, DEFINITION);
    }

    }
