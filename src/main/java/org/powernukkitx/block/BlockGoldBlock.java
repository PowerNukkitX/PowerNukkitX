package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockGoldBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(GOLD_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3)
            .resistance(30)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_IRON)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGoldBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGoldBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Gold Block";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}