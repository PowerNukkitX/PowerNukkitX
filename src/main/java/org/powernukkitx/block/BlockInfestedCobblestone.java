package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockInfestedCobblestone extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(INFESTED_COBBLESTONE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1)
            .resistance(0.75)
            .toolType(ItemTool.TYPE_PICKAXE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInfestedCobblestone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInfestedCobblestone(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Infested Cobblestone";
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    
}
