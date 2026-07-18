package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDiamondBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(DIAMOND_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(5)
            .resistance(30)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_IRON)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDiamondBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDiamondBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Diamond Block";
    }

    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}