package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;


public class BlockMossCarpet extends BlockCarpet {

    public static final BlockProperties PROPERTIES = new BlockProperties(MOSS_CARPET);
    public static final BlockDefinition DEFINITION = BlockCarpet.DEFINITION.toBuilder()
            .resistance(0.1)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMossCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMossCarpet(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Moss Carpet";
    }

    
    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }
}
