package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockHoneycombBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(HONEYCOMB_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.6)
            .resistance(0.6)
            .toolType(ItemTool.TYPE_HANDS_ONLY)
            .canHarvestWithHand(true)
            .build();

    public BlockHoneycombBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockHoneycombBlock(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    
    @Override
    public String getName() {
        return "Honeycomb Block";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

}
