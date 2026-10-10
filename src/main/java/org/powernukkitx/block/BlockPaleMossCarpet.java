package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;


public class BlockPaleMossCarpet extends BlockMossCarpet {

    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_MOSS_CARPET,
            CommonBlockProperties.PALE_MOSS_CARPET_SIDE_EAST,
            CommonBlockProperties.PALE_MOSS_CARPET_SIDE_NORTH,
            CommonBlockProperties.PALE_MOSS_CARPET_SIDE_SOUTH,
            CommonBlockProperties.PALE_MOSS_CARPET_SIDE_WEST,
            CommonBlockProperties.UPPER_BLOCK_BIT);
    public static final BlockDefinition DEFINITION = BlockMossCarpet.DEFINITION.toBuilder()
            .resistance(0.1)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleMossCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleMossCarpet(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Pale Moss Carpet";
    }

    
    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }
}
