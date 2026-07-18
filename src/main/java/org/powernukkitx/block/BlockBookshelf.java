package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author Nukkit Project Team
 */
public class BlockBookshelf extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(BOOKSHELF);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5D)
            .resistance(7.5D)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(30)
            .burnAbility(20)
            .build();

    public BlockBookshelf() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockBookshelf(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockBookshelf(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Bookshelf";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(ItemID.BOOK, 0, 3)
        };
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
