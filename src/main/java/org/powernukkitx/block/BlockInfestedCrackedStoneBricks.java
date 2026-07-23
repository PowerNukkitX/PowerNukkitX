package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockInfestedCrackedStoneBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(INFESTED_CRACKED_STONE_BRICKS);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.75)
            .resistance(0.75)
            .toolType(ItemTool.TYPE_PICKAXE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInfestedCrackedStoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInfestedCrackedStoneBricks(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Infested Cracked Stone Bricks";
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    
}
