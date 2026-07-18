package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCrackedNetherBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRACKED_NETHER_BRICKS);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrackedNetherBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedNetherBricks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Cracked Nether Bricks";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}