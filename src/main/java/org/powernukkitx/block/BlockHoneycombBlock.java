package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockHoneycombBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(HONEYCOMB_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.6f)
            .resistance(0.6f)
            .toolType(ItemTool.TYPE_HANDS_ONLY)
            .canHarvestWithHand(true)
            .build();

    public BlockHoneycombBlock() {
        this(PROPERTIES.getDefaultState());
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
