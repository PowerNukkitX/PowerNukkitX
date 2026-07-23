package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockInfestedChiseledStoneBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(INFESTED_CHISELED_STONE_BRICKS);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.75)
            .resistance(0.75)
            .toolType(ItemTool.TYPE_PICKAXE)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInfestedChiseledStoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInfestedChiseledStoneBricks(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Infested Chiseled Stone Bricks";
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

}
