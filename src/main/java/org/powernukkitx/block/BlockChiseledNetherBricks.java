package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockChiseledNetherBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_NETHER_BRICKS);
    public static final BlockDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .hardness(2)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledNetherBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledNetherBricks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Chiseled Nether Bricks";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}