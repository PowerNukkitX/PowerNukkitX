package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;

public abstract class BlockDoubleWoodenSlab extends BlockDoubleSlabBase {
    public static final BlockDefinition DEFINITION = BlockDoubleSlabBase.DEFINITION.toBuilder()
            .resistance(15)
            .toolType(ItemTool.TYPE_AXE)
            .build();
    public BlockDoubleWoodenSlab(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockDoubleWoodenSlab(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }
    @Override
    public String getName() {
        return "Double " + getSlabName() + " Wood Slab";
    }

    @Override
    protected boolean isCorrectTool(Item item) {
        return true;
    }
}