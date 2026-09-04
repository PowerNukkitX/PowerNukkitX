package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockInfestedStone extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(INFESTED_STONE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.75)
            .resistance(0.75)
            .toolType(ItemTool.TYPE_PICKAXE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInfestedStone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInfestedStone(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Infested Stone";
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    
}
